package vi.wbca.webcinema.chatbot.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.chatbot.ai.GroqClient;
import vi.wbca.webcinema.chatbot.model.MovieFilter;
import vi.wbca.webcinema.chatbot.model.request.ChatRequest;
import vi.wbca.webcinema.chatbot.model.response.ChatResponse;
import vi.wbca.webcinema.chatbot.service.ChatBotService;
import vi.wbca.webcinema.enums.BillStatusEnum;
import vi.wbca.webcinema.model.entity.bill.BillStatus;
import vi.wbca.webcinema.model.entity.bill.Promotion;
import vi.wbca.webcinema.model.entity.bill.Bill;
import vi.wbca.webcinema.model.entity.movie.Movie;
import vi.wbca.webcinema.model.entity.movie.Schedule;
import vi.wbca.webcinema.model.entity.movie.MovieType;
import vi.wbca.webcinema.repository.bill.BillStatusRepo;
import vi.wbca.webcinema.repository.bill.PromotionRepo;
import vi.wbca.webcinema.repository.bill.BillRepo;
import vi.wbca.webcinema.repository.movie.MovieRepo;
import vi.wbca.webcinema.repository.movie.MovieTypeRepo;
import vi.wbca.webcinema.repository.movie.ScheduleRepo;
import vi.wbca.webcinema.model.entity.user.User;
import vi.wbca.webcinema.repository.user.UserRepo;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatBotServiceImpl implements ChatBotService {

    private final MovieRepo movieRepo;
    private final MovieTypeRepo movieTypeRepo;
    private final ScheduleRepo scheduleRepo;
    private final PromotionRepo promotionRepo;
    private final BillRepo billRepo;
    private final BillStatusRepo billStatusRepo;
    private final UserRepo userRepo;
    private final GroqClient groqClient;
    private final MessageSource messageSource;

    @Override
    @Transactional(readOnly = true)
    public ChatResponse chat(ChatRequest request) {
        Locale locale = LocaleContextHolder.getLocale();
        boolean respondInEnglish = isEnglish(locale);

        if (request == null || request.getMessage() == null || request.getMessage().isBlank()) {
            return new ChatResponse(message(locale, "chatbot.empty_request"));
        }

        String message = request.getMessage().toLowerCase(Locale.ROOT);
        boolean wantsPromotions = wantsPromotions(message);
        boolean wantsMovieRelated = wantsMovieRelated(message);
        boolean wantsRecommendation = wantsRecommendation(message);
        List<Movie> bookedMovies = wantsRecommendation ? getBookedMovies(request.getUserId()) : List.of();

        if (wantsPromotions && !hasActivePromotions()) {
            return new ChatResponse(message(locale, "chatbot.promotions.none"));
        }

        // 1. Detect intent
        MovieFilter filter = detectIntent(message);

        // 2. Query DB
        List<Movie> allMovies = getMovies(filter);
        List<Movie> movies = wantsRecommendation ? filterUnseenMovies(allMovies, bookedMovies) : allMovies;

        if (wantsMovieRelated) {
            return new ChatResponse(buildMovieResponse(message, movies, allMovies, wantsRecommendation, locale, respondInEnglish));
        }

        // 3. Build context
        String context = buildContext(movies, bookedMovies, message, wantsMovieRelated, wantsPromotions, locale, respondInEnglish);

        // 4. Call AI (format only)
        String result = groqClient.ask(buildPrompt(context, message));

        return new ChatResponse(result);
    }

    // ================= INTENT =================

    private MovieFilter detectIntent(String message) {
        MovieFilter f = new MovieFilter();
        boolean hasExplicitTimePreference = false;

        if (message.contains("đang chiếu")) {
            f.setNowShowing(true);
            hasExplicitTimePreference = true;
        }
        if (message.contains("sắp chiếu")) {
            f.setComingSoon(true);
            hasExplicitTimePreference = true;
        }
        String resolvedGenre = resolveGenreFromMessage(message);
        if (!resolvedGenre.isBlank()) {
            f.setGenre(resolvedGenre);
        }

        // Chỉ mặc định đang chiếu khi user không chỉ rõ genre hoặc mốc thời gian.
        if (!hasExplicitTimePreference && resolvedGenre.isBlank()) {
            f.setNowShowing(true);
        }
        return f;
    }

    private String resolveGenreFromMessage(String message) {
        String normalizedMessage = normalizeText(message);

        return movieTypeRepo.findAll().stream()
                .filter(MovieType::isActive)
                .map(MovieType::getMovieTypeNameVi)
                .filter(genre -> genre != null && !genre.isBlank())
                .filter(genre -> normalizedMessage.contains(normalizeText(genre)))
                .findFirst()
                .orElse("");
    }

    // ================= DB QUERY =================

    private List<Movie> getMovies(MovieFilter f) {
        LocalDateTime now = LocalDateTime.now();
        return movieRepo.findAll().stream()
            .filter(Movie::isActive)
            .filter(movie -> !f.isNowShowing() || (movie.getPremiereDate() != null
                && !movie.getPremiereDate().isAfter(now)
                && (movie.getEndDate() == null || !movie.getEndDate().isBefore(now))))
            .filter(movie -> !f.isComingSoon() || (movie.getPremiereDate() != null && movie.getPremiereDate().isAfter(now)))
            .filter(movie -> f.getGenre() == null || f.getGenre().isBlank() || hasGenre(movie, f.getGenre()))
            .sorted(Comparator.comparing(Movie::getPremiereDate, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
            .limit(5)
            .toList();
    }

        private boolean hasGenre(Movie movie, String genre) {
        if (movie == null || movie.getMovieTypes() == null || genre == null || genre.isBlank()) {
            return false;
        }

        String normalizedGenre = normalizeText(genre);
        return movie.getMovieTypes().stream()
            .map(MovieType::getMovieTypeNameVi)
            .filter(value -> value != null && !value.isBlank())
            .anyMatch(value -> normalizeText(value).equals(normalizedGenre));
        }

    // ================= CONTEXT =================

    private String buildContext(List<Movie> movies, List<Movie> bookedMovies, String question, boolean wantsMovieRelated, boolean wantsPromotions, Locale locale, boolean respondInEnglish) {
        boolean wantsShowtimes = containsAny(question, "suất chiếu", "lich chieu", "lịch chiếu", "gio chieu", "giờ chiếu", "chieu luc nao");

        StringBuilder context = new StringBuilder();

        if (wantsMovieRelated && bookedMovies != null && !bookedMovies.isEmpty()) {
            context.append(message(locale, "chatbot.context.booked_movies"));
            bookedMovies.stream()
                    .distinct()
                    .limit(5)
                    .forEach(movie -> context.append("- ").append(movie.getName()).append("\n"));
            String preferredGenres = buildPreferredGenres(bookedMovies);
            if (!preferredGenres.isBlank()) {
                context.append(message(locale, "chatbot.context.preferred_genres"))
                        .append(preferredGenres)
                        .append("\n");
            }
            context.append("\n");
        }
        if (wantsMovieRelated && movies != null && !movies.isEmpty()) {
            context.append(message(locale, "chatbot.context.available_movies"));
            movies.stream()
                    .distinct()
                    .limit(5)
                    .forEach(movie -> context.append("- ")
                            .append(movie.getName())
                            .append(formatMovieTypes(movie, locale, respondInEnglish))
                            .append("\n"));
            context.append("\n");
        }
        if (wantsShowtimes && !movies.isEmpty()) {
            appendShowtimes(context, movies, question, locale, respondInEnglish);
        }
        if (wantsPromotions) {
            appendPromotions(context, locale, respondInEnglish);
        }
        return context.toString().trim();
    }

    private List<Movie> getBookedMovies(Long userId) {
        if (userId == null) {
            return List.of();
        }
        User user = userRepo.findById(userId).orElse(null);
        if (user == null) {
            return List.of();
        }
        BillStatus successStatus = billStatusRepo.findByName(BillStatusEnum.SUCCESS.name()).orElse(null);
        if (successStatus == null) {
            return List.of();
        }
        List<Bill> bills = billRepo.findAllByUserAndBillStatusOrderByPaidAtDesc(user, successStatus);
        if (bills.isEmpty()) {
            return List.of();
        }
        LinkedHashSet<Movie> movies = new LinkedHashSet<>();
        for (Bill bill : bills) {
            if (bill.getBillTickets() == null) {
                continue;
            }
            bill.getBillTickets().forEach(billTicket -> {
                if (billTicket.getTicket() != null
                        && billTicket.getTicket().getSchedule() != null
                        && billTicket.getTicket().getSchedule().getMovie() != null) {
                    movies.add(billTicket.getTicket().getSchedule().getMovie());
                }
            });
        }

        return movies.stream().toList();
    }

    private String buildPreferredGenres(List<Movie> bookedMovies) {
        Map<String, Long> genreCounts = new HashMap<>();

        for (Movie movie : bookedMovies) {
            if (movie.getMovieTypes() == null) {
                continue;
            }
            for (MovieType movieType : movie.getMovieTypes()) {
                String genre = movieType.getMovieTypeNameVi();
                if (genre != null && !genre.isBlank()) {
                    genreCounts.put(genre, genreCounts.getOrDefault(genre, 0L) + 1);
                }
            }
        }

        return genreCounts.entrySet().stream()
                .sorted((left, right) -> Long.compare(right.getValue(), left.getValue()))
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.joining(", "));
    }

    private String formatMovieTypes(Movie movie, Locale locale, boolean respondInEnglish) {
        if (movie == null || movie.getMovieTypes() == null || movie.getMovieTypes().isEmpty()) {
            return "";
        }
        String genres = movie.getMovieTypes().stream()
                .map(MovieType::getMovieTypeNameVi)
                .filter(genre -> genre != null && !genre.isBlank())
                .distinct()
                .limit(3)
                .collect(Collectors.joining(", "));

        if (genres.isBlank()) {
            return "";
        }
        return message(locale, "chatbot.genre_label") + genres;
    }

    private List<Movie> filterUnseenMovies(List<Movie> movies, List<Movie> bookedMovies) {
        if (movies == null || movies.isEmpty()) {
            return List.of();
        }
        if (bookedMovies == null || bookedMovies.isEmpty()) {
            return movies;
        }

        LinkedHashSet<Long> watchedMovieIds = bookedMovies.stream()
                .map(Movie::getId)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        return movies.stream()
                .filter(movie -> movie.getId() == null || !watchedMovieIds.contains(movie.getId()))
                .toList();
    }

    private String buildMovieResponse(String question, List<Movie> movies, List<Movie> allMovies, boolean wantsRecommendation, Locale locale, boolean respondInEnglish) {
        boolean wantsShowtimes = containsAny(question, "suất chiếu", "lich chieu", "lịch chiếu", "gio chieu", "giờ chiếu", "chieu luc nao");
        boolean wantsComingSoon = containsAny(question, "sắp chiếu", "sap chieu", "phim sap chieu", "phim sắp chiếu");
        String requestedGenre = resolveGenreFromMessage(question);

        if (wantsComingSoon) {
            if (movies == null || movies.isEmpty()) {
                return message(locale, "chatbot.movie.upcoming.none");
            }
            StringBuilder upcomingResponse = new StringBuilder();
            if (!requestedGenre.isBlank()) {
                upcomingResponse.append(message(locale, "chatbot.movie.upcoming.genre_header", requestedGenre));
            } else {
                upcomingResponse.append(message(locale, "chatbot.movie.upcoming.header"));
            }
            upcomingResponse.append("\n");
            for (Movie movie : movies) {
                upcomingResponse.append("- ")
                        .append(movie.getName())
                        .append(formatMovieTypes(movie, locale, respondInEnglish));

                if (movie.getPremiereDate() != null) {
                    upcomingResponse.append(message(locale, "chatbot.movie.release_date"))
                            .append(movie.getPremiereDate().toLocalDate());
                }
                upcomingResponse.append("\n");
            }
            return upcomingResponse.toString().trim();
        }

        if (movies == null || movies.isEmpty()) {
            if (!requestedGenre.isBlank()) {
                return message(locale, "chatbot.movie.no_movies_in_genre", requestedGenre);
            }
            String currentShowingTitles = buildCurrentShowingTitles(allMovies);
            if (!currentShowingTitles.isBlank()) {
                return message(locale, "chatbot.movie.no_now_showing_with_titles", currentShowingTitles);
            }
            String alternativeGenres = buildCurrentGenreHint();
            if (!alternativeGenres.isBlank()) {
                return message(locale, "chatbot.movie.no_matching_movies_with_genres", alternativeGenres);
            }
            return message(locale, "chatbot.movie.no_matching_movies");
        }

        StringBuilder response = new StringBuilder();

        if (!requestedGenre.isBlank()) {
            response.append(message(locale, "chatbot.movie.genre_header", requestedGenre));
        } else if (wantsRecommendation) {
            response.append(message(locale, "chatbot.movie.recommendation_header"));
        } else if (wantsShowtimes) {
            response.append(message(locale, "chatbot.movie.showtimes_header"));
        } else {
            response.append(message(locale, "chatbot.movie.now_showing_header"));
        }
        response.append("\n");

        for (Movie movie : movies) {
            response.append("- ")
                    .append(movie.getName())
                    .append(formatMovieTypes(movie, locale, respondInEnglish));
            if (movie.getPremiereDate() != null) {
                response.append(message(locale, "chatbot.movie.release_date"))
                        .append(movie.getPremiereDate().toLocalDate());
            }
            response.append("\n");
        }
        return response.toString().trim();
    }

    private String buildCurrentGenreHint() {
        return movieRepo.findAll().stream()
                .filter(Movie::isActive)
                .filter(movie -> movie.getPremiereDate() != null && !movie.getPremiereDate().isAfter(LocalDateTime.now()))
                .filter(movie -> movie.getEndDate() == null || !movie.getEndDate().isBefore(LocalDateTime.now()))
                .sorted(Comparator.comparing(Movie::getPremiereDate, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .flatMap(movie -> movie.getMovieTypes() == null ? java.util.stream.Stream.empty() : movie.getMovieTypes().stream())
                .map(MovieType::getMovieTypeNameVi)
                .filter(genre -> genre != null && !genre.isBlank())
                .distinct()
                .limit(3)
                .collect(Collectors.joining(", "));
    }

    private String buildCurrentShowingTitles(List<Movie> movies) {
        if (movies == null || movies.isEmpty()) {
            return "";
        }

        return movies.stream()
                .filter(Movie::isActive)
                .filter(movie -> movie.getPremiereDate() != null && !movie.getPremiereDate().isAfter(LocalDateTime.now()))
                .filter(movie -> movie.getEndDate() == null || !movie.getEndDate().isBefore(LocalDateTime.now()))
                .map(Movie::getName)
                .filter(name -> name != null && !name.isBlank())
                .distinct()
                .limit(3)
                .collect(Collectors.joining(", "));
    }

    private void appendShowtimes(StringBuilder context, List<Movie> movies, String question, Locale locale, boolean respondInEnglish) {
        List<Movie> resolvedMovies = findMatchingMovies(question, movies);
        if (resolvedMovies.isEmpty()) {
            resolvedMovies = movies;
        }
        final List<Movie> targetMovies = resolvedMovies;

        List<Schedule> schedules = scheduleRepo.findActiveSchedulesOverlapping(
                        LocalDateTime.now(),
                        LocalDateTime.now().plusDays(14))
                .stream()
                .filter(schedule -> schedule.getMovie() != null && targetMovies.stream()
                        .anyMatch(movie -> movie.getId().equals(schedule.getMovie().getId())))
                .sorted(Comparator.comparing(Schedule::getStartAt))
                .limit(10)
                .toList();

        if (schedules.isEmpty()) {
            return;
        }

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        context.append(message(locale, "chatbot.movie.showtimes_context"));
        for (Schedule schedule : schedules) {
            context.append("- ")
                    .append(schedule.getMovie().getName())
                    .append(" | ")
                    .append(schedule.getRoom() != null && schedule.getRoom().getCinema() != null
                            ? schedule.getRoom().getCinema().getNameOfCinema()
                            : "")
                .append(message(locale, "chatbot.movie.room"))
                    .append(schedule.getRoom() != null ? schedule.getRoom().getName() : "")
                    .append(" | ")
                    .append(schedule.getStartAt() != null ? schedule.getStartAt().format(dateTimeFormatter) : "")
                    .append("\n");
        }
    }

    private boolean hasActivePromotions() {
        return promotionRepo.findAll().stream()
                .anyMatch(promotion -> promotion.isActive()
                        && (promotion.getStartTime() == null || !promotion.getStartTime().isAfter(LocalDateTime.now()))
                        && (promotion.getEndTime() == null || !promotion.getEndTime().isBefore(LocalDateTime.now())));
    }

    private void appendPromotions(StringBuilder context, Locale locale, boolean respondInEnglish) {
        List<Promotion> promotions = promotionRepo.findAll().stream()
                .filter(Promotion::isActive)
                .filter(promotion -> promotion.getStartTime() == null || !promotion.getStartTime().isAfter(LocalDateTime.now()))
                .filter(promotion -> promotion.getEndTime() == null || !promotion.getEndTime().isBefore(LocalDateTime.now()))
                .sorted(Comparator.comparing(Promotion::getEndTime, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();

        if (promotions.isEmpty()) {
            return;
        }

        context.append(message(locale, "chatbot.promotions.header"));
        for (Promotion promotion : promotions) {
            context.append("- ")
                    .append(promotion.getName())
                    .append(" | ")
                    .append(promotion.getPercent() != null ? promotion.getPercent() + "%" : "")
                    .append(" | ")
                    .append(promotion.getDescription() != null ? promotion.getDescription() : "")
                    .append(message(locale, "chatbot.promotions.expiry"))
                    .append(promotion.getEndTime() != null ? promotion.getEndTime().toLocalDate() : LocalDate.now())
                    .append("\n");
        }
    }

    private List<Movie> findMatchingMovies(String question, List<Movie> fallbackMovies) {
        String normalizedQuestion = normalizeText(question);
        List<Movie> activeMovies = movieRepo.findAll().stream()
                .filter(Movie::isActive).toList();

        List<Movie> matchedMovies = activeMovies.stream()
                .filter(movie -> normalizedQuestion.contains(normalizeText(movie.getName()))).toList();

        if (!matchedMovies.isEmpty()) {
            return matchedMovies;
        }
        return fallbackMovies.stream()
                .filter(movie -> normalizedQuestion.contains(normalizeText(movie.getName()))).toList();
    }

    private boolean containsAny(String text, String... keywords) {
        String normalizedText = normalizeText(text);
        for (String keyword : keywords) {
            if (normalizedText.contains(normalizeText(keyword))) {
                return true;
            }
        }
        return false;
    }

    private boolean isEnglish(Locale locale) {
        return locale != null && Locale.ENGLISH.getLanguage().equals(locale.getLanguage());
    }

    private String message(Locale locale, String key, Object... args) {
        return messageSource.getMessage(key, args, locale);
    }

    private boolean wantsPromotions(String message) {
        return containsAny(message, "khuyến mãi", "khuyen mai", "ưu đãi",
                            "uu dai", "giảm giá", "giam gia", "sale", "promo");
    }

    private boolean wantsMovieRelated(String message) {
        return containsAny(message, "phim gì", "co phim gi", "có phim gì", "goi y phim", "gợi ý phim", "de xuat phim",
                "đề xuất phim", "phim de xem", "phim để xem", "xem phim", "phim nao", "phim nào", "the loai", "thể loại",
                "phim", "movie", "đang chiếu", "sắp chiếu", "suất chiếu", "lịch chiếu");
    }

    private boolean wantsRecommendation(String message) {
        return containsAny(message, "phù hợp với tôi", "phu hop voi toi", "gợi ý cho tôi", "goi y cho toi",
                "đề xuất cho tôi", "de xuat cho toi", "nên xem", "nen xem", "cho tôi phim", "chon phim", "phim hợp với tôi",
                "phim phu hop voi toi");
    }

    private String normalizeText(String input) {
        if (input == null) {
            return "";
        }
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return normalized.toLowerCase(Locale.ROOT);
    }

    // ================= PROMPT =================

    private String buildPrompt(String context, String question) {
        return """
            You are a movie-cinema assistant chatbot.
            Use only the data below. Do not invent any information.
            Data: %s
            User question: %s
            - Answer in the same language as the user's question with a warm, friendly, and natural tone.
            - Do not add greetings or opening phrases.
            - Keep the response concise, clear, and easy to read.
            - Use line breaks when they improve readability.
            - Do not mention that the response is based on the provided data.
            - If the question is about promotions and there is no active promotion data, reply exactly in the user's language.
            - Do not add any extra sentence after that exact no-promotion response.
            - Only use the user's booking history when the question is about movies or asks for movie suggestions.
            - When recommending movies, suggest only movies the user has not watched yet.
            - Do not recommend any movie that is already in the user's paid viewing history.
            - Do not mention movies, booking history, or showtimes when the user only asks about promotions.
        """.formatted(context, question);
    }
}
