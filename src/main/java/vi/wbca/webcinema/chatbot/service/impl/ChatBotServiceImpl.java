package vi.wbca.webcinema.chatbot.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vi.wbca.webcinema.chatbot.ai.GroqClient;
import vi.wbca.webcinema.chatbot.model.MovieFilter;
import vi.wbca.webcinema.chatbot.model.request.ChatRequest;
import vi.wbca.webcinema.chatbot.model.response.ChatResponse;
import vi.wbca.webcinema.chatbot.service.ChatBotService;
import vi.wbca.webcinema.enums.BillStatusEnum;
import vi.wbca.webcinema.enums.RoomTypeEnum;
import vi.wbca.webcinema.enums.SeatTypeEnum;
import vi.wbca.webcinema.model.entity.bill.Bill;
import vi.wbca.webcinema.model.entity.bill.BillStatus;
import vi.wbca.webcinema.model.entity.bill.Promotion;
import vi.wbca.webcinema.model.entity.cinema.Cinema;
import vi.wbca.webcinema.model.entity.movie.Movie;
import vi.wbca.webcinema.model.entity.movie.MovieType;
import vi.wbca.webcinema.model.entity.movie.Schedule;
import vi.wbca.webcinema.model.entity.setting.GeneralSetting;
import vi.wbca.webcinema.model.entity.user.User;
import vi.wbca.webcinema.repository.bill.BillRepo;
import vi.wbca.webcinema.repository.bill.BillStatusRepo;
import vi.wbca.webcinema.repository.bill.PromotionRepo;
import vi.wbca.webcinema.repository.cinema.CinemaRepo;
import vi.wbca.webcinema.repository.movie.MovieRepo;
import vi.wbca.webcinema.repository.movie.MovieTypeRepo;
import vi.wbca.webcinema.repository.movie.ScheduleRepo;
import vi.wbca.webcinema.repository.setting.GeneralSettingRepo;
import vi.wbca.webcinema.repository.user.UserRepo;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatBotServiceImpl implements ChatBotService {

    private static final Locale EN_LOCALE = Locale.ENGLISH;

    private final MovieRepo movieRepo;
    private final MovieTypeRepo movieTypeRepo;
    private final ScheduleRepo scheduleRepo;
    private final PromotionRepo promotionRepo;
    private final BillRepo billRepo;
    private final BillStatusRepo billStatusRepo;
    private final UserRepo userRepo;
    private final CinemaRepo cinemaRepo;
    private final GeneralSettingRepo generalSettingRepo;
    private final GroqClient groqClient;
    private final MessageSource messageSource;

    @Override
    @Transactional(readOnly = true)
    public ChatResponse chat(ChatRequest request) {
        Locale locale = LocaleContextHolder.getLocale();

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

        // Handle ticket price queries
        if (wantsTicketPrice(message)) {
            return new ChatResponse(buildTicketPriceResponse(locale));
        }

        // Handle cinema address/info queries
        if (wantsCinemaInfo(message)) {
            // Use original message to preserve proper casing of location names (e.g. "Hà Nội" not "hà nội")
            String location = extractLocationFromMessage(request.getMessage());
            return new ChatResponse(buildCinemaInfoResponse(location, locale));
        }

        // Handle date-specific showtime queries first (e.g. "07/06 có phim gì")
        // Only trigger if message also contains movie/showtime related keywords to avoid false positives
        boolean wantsDateShowtimes = containsAny(message,
                "phim gì", "có phim", "co phim", "phim nào", "phim nao",
                "suất chiếu", "suat chieu", "lịch chiếu", "lich chieu",
                "đang chiếu", "dang chieu", "chiếu", "chieu",
                "what movie", "showing", "showtimes");
        LocalDate queriedDate = wantsDateShowtimes ? extractDateFromMessage(message) : null;
        if (queriedDate != null) {
            return new ChatResponse(buildDateShowtimesResponse(queriedDate, locale));
        }

        List<Movie> titleMatchedMovies = findMatchingMovies(message, locale, List.of());
        if (!titleMatchedMovies.isEmpty()) {
            String context = buildMovieDetailContext(titleMatchedMovies.get(0), locale);
            try {
                return new ChatResponse(groqClient.ask(buildPrompt(context, message)));
            } catch (Exception e) {
                return new ChatResponse(message(locale, "chatbot.ai_unavailable"));
            }
        }

        MovieFilter filter = detectIntent(message, locale);
        List<Movie> allMovies = getMovies(filter, locale);
        List<Movie> movies = wantsRecommendation ? filterUnseenMovies(allMovies, bookedMovies) : allMovies;

        if (wantsMovieRelated) {
            return new ChatResponse(buildMovieResponse(message, movies, allMovies, wantsRecommendation, locale));
        }

        String context = buildContext(movies, bookedMovies, message, wantsMovieRelated, wantsPromotions, locale);
        try {
            return new ChatResponse(groqClient.ask(buildPrompt(context, message)));
        } catch (Exception e) {
            return new ChatResponse(message(locale, "chatbot.ai_unavailable"));
        }
    }

    private MovieFilter detectIntent(String message, Locale locale) {
        MovieFilter filter = new MovieFilter();
        boolean hasExplicitTimePreference = false;

        if (message.contains("đang chiếu") || message.contains("now showing")) {
            filter.setNowShowing(true);
            hasExplicitTimePreference = true;
        }
        if (message.contains("sắp chiếu") || message.contains("coming soon") || message.contains("upcoming")) {
            filter.setComingSoon(true);
            hasExplicitTimePreference = true;
        }

        String resolvedGenre = resolveGenreFromMessage(message, locale);
        if (!resolvedGenre.isBlank()) {
            filter.setGenre(resolvedGenre);
        }

        if (!hasExplicitTimePreference && resolvedGenre.isBlank()) {
            filter.setNowShowing(true);
        }
        return filter;
    }

    private String resolveGenreFromMessage(String message, Locale locale) {
        String normalizedMessage = normalizeText(message);

        return movieTypeRepo.findAll().stream()
                .filter(MovieType::isActive)
                .map(movieType -> getLocalizedMovieTypeName(movieType, locale))
                .filter(genre -> genre != null && !genre.isBlank())
                .filter(genre -> normalizedMessage.contains(normalizeText(genre)))
                .findFirst()
                .orElse("");
    }

    private List<Movie> getMovies(MovieFilter filter, Locale locale) {
        LocalDateTime now = LocalDateTime.now();
        return movieRepo.findAll().stream()
                .filter(Movie::isActive)
                .filter(movie -> !filter.isNowShowing() || (movie.getPremiereDate() != null
                        && !movie.getPremiereDate().isAfter(now)
                        && (movie.getEndDate() == null || !movie.getEndDate().isBefore(now))))
                .filter(movie -> !filter.isComingSoon() || (movie.getPremiereDate() != null && movie.getPremiereDate().isAfter(now)))
                .filter(movie -> filter.getGenre() == null || filter.getGenre().isBlank() || hasGenre(movie, filter.getGenre(), locale))
                .sorted(Comparator.comparing(Movie::getPremiereDate, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .limit(5)
                .toList();
    }

    private boolean hasGenre(Movie movie, String genre, Locale locale) {
        if (movie == null || movie.getMovieTypes() == null || genre == null || genre.isBlank()) {
            return false;
        }

        String normalizedGenre = normalizeText(genre);
        return movie.getMovieTypes().stream()
                .map(movieType -> getLocalizedMovieTypeName(movieType, locale))
                .filter(value -> value != null && !value.isBlank())
                .anyMatch(value -> normalizeText(value).equals(normalizedGenre));
    }

    private String buildContext(List<Movie> movies, List<Movie> bookedMovies, String question, boolean wantsMovieRelated, boolean wantsPromotions, Locale locale) {
        boolean wantsShowtimes = containsAny(question, "suất chiếu", "lich chieu", "lịch chiếu", "gio chieu", "giờ chiếu", "chieu luc nao", "showtimes", "screening");
        StringBuilder context = new StringBuilder();

        if (wantsMovieRelated && bookedMovies != null && !bookedMovies.isEmpty()) {
            context.append(message(locale, "chatbot.context.booked_movies"));
            bookedMovies.stream().distinct().limit(5)
                    .forEach(movie -> context.append("- ").append(getLocalizedMovieName(movie, locale)).append("\n"));
            String preferredGenres = buildPreferredGenres(bookedMovies, locale);
            if (!preferredGenres.isBlank()) {
                context.append(message(locale, "chatbot.context.preferred_genres")).append(preferredGenres).append("\n");
            }
            context.append("\n");
        }

        if (wantsMovieRelated && movies != null && !movies.isEmpty()) {
            context.append(message(locale, "chatbot.context.available_movies"));
            movies.stream().distinct().limit(5)
                    .forEach(movie -> context.append("- ")
                            .append(getLocalizedMovieName(movie, locale))
                            .append(formatMovieTypes(movie, locale))
                            .append("\n"));
            context.append("\n");
        }

        if (wantsShowtimes && !movies.isEmpty()) {
            appendShowtimes(context, movies, question, locale);
        }
        if (wantsPromotions) {
            appendPromotions(context, locale);
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

    private String buildPreferredGenres(List<Movie> bookedMovies, Locale locale) {
        Map<String, Long> genreCounts = new HashMap<>();

        for (Movie movie : bookedMovies) {
            if (movie.getMovieTypes() == null) {
                continue;
            }
            for (MovieType movieType : movie.getMovieTypes()) {
                String genre = getLocalizedMovieTypeName(movieType, locale);
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

    private String formatMovieTypes(Movie movie, Locale locale) {
        if (movie == null || movie.getMovieTypes() == null || movie.getMovieTypes().isEmpty()) {
            return "";
        }

        String genres = movie.getMovieTypes().stream()
                .map(movieType -> getLocalizedMovieTypeName(movieType, locale))
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

    private String buildMovieResponse(String question, List<Movie> movies, List<Movie> allMovies, boolean wantsRecommendation, Locale locale) {
        boolean wantsShowtimes = containsAny(question, "suất chiếu", "lich chieu", "lịch chiếu", "gio chieu", "giờ chiếu", "chieu luc nao", "showtimes", "screening");
        boolean wantsComingSoon = containsAny(question, "sắp chiếu", "sap chieu", "phim sap chieu", "phim sắp chiếu", "coming soon", "upcoming");
        String requestedGenre = resolveGenreFromMessage(question, locale);
        boolean upcomingOnly = isUpcomingOnly(movies);

        if (wantsComingSoon || (upcomingOnly && !wantsShowtimes)) {
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
                        .append(getLocalizedMovieName(movie, locale))
                        .append(formatMovieTypes(movie, locale));

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
            String currentShowingTitles = buildCurrentShowingTitles(allMovies, locale);
            if (!currentShowingTitles.isBlank()) {
                return message(locale, "chatbot.movie.no_now_showing_with_titles", currentShowingTitles);
            }
            String alternativeGenres = buildCurrentGenreHint(locale);
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
        } else if (upcomingOnly) {
            response.append(message(locale, "chatbot.movie.upcoming.header"));
        } else {
            response.append(message(locale, "chatbot.movie.now_showing_header"));
        }
        response.append("\n");

        for (Movie movie : movies) {
            response.append("- ")
                    .append(getLocalizedMovieName(movie, locale))
                    .append(formatMovieTypes(movie, locale));
            if (movie.getPremiereDate() != null) {
                response.append(message(locale, "chatbot.movie.release_date"))
                        .append(movie.getPremiereDate().toLocalDate());
            }
            response.append("\n");
        }
        return response.toString().trim();
    }

    private String buildCurrentGenreHint(Locale locale) {
        return movieRepo.findAll().stream()
                .filter(Movie::isActive)
                .filter(movie -> movie.getPremiereDate() != null && !movie.getPremiereDate().isAfter(LocalDateTime.now()))
                .filter(movie -> movie.getEndDate() == null || !movie.getEndDate().isBefore(LocalDateTime.now()))
                .sorted(Comparator.comparing(Movie::getPremiereDate, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .flatMap(movie -> movie.getMovieTypes() == null ? java.util.stream.Stream.empty() : movie.getMovieTypes().stream())
                .map(movieType -> getLocalizedMovieTypeName(movieType, locale))
                .filter(genre -> genre != null && !genre.isBlank())
                .distinct()
                .limit(3)
                .collect(Collectors.joining(", "));
    }

    private String buildCurrentShowingTitles(List<Movie> movies, Locale locale) {
        if (movies == null || movies.isEmpty()) {
            return "";
        }

        return movies.stream()
                .filter(Movie::isActive)
                .filter(movie -> movie.getPremiereDate() != null && !movie.getPremiereDate().isAfter(LocalDateTime.now()))
                .filter(movie -> movie.getEndDate() == null || !movie.getEndDate().isBefore(LocalDateTime.now()))
                .map(movie -> getLocalizedMovieName(movie, locale))
                .filter(name -> name != null && !name.isBlank())
                .distinct()
                .limit(3)
                .collect(Collectors.joining(", "));
    }

    private void appendShowtimes(StringBuilder context, List<Movie> movies, String question, Locale locale) {
        List<Movie> resolvedMovies = findMatchingMovies(question, locale, movies);
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
                    .append(getLocalizedMovieName(schedule.getMovie(), locale))
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

    private void appendPromotions(StringBuilder context, Locale locale) {
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

    private List<Movie> findMatchingMovies(String question, Locale locale, List<Movie> fallbackMovies) {
        String normalizedQuestion = normalizeText(question);
        List<Movie> activeMovies = movieRepo.findAll().stream()
                .filter(Movie::isActive)
                .toList();

        List<Movie> matchedMovies = activeMovies.stream()
                .filter(movie -> movie != null && !normalizedQuestion.isBlank())
                .filter(movie -> matchesLocalizedText(normalizedQuestion, normalizeText(getLocalizedMovieName(movie, locale))))
                .toList();

        if (!matchedMovies.isEmpty()) {
            return matchedMovies;
        }

        return fallbackMovies.stream()
                .filter(movie -> movie != null && !normalizedQuestion.isBlank())
                .filter(movie -> matchesLocalizedText(normalizedQuestion, normalizeText(getLocalizedMovieName(movie, locale))))
                .toList();
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

    /**
     * Extract a specific date from the user message.
     * Supports formats: dd/MM, dd/MM/yyyy, dd-MM, dd-MM-yyyy.
     * When the year is omitted, the current year is assumed.
     */
    private LocalDate extractDateFromMessage(String message) {
        // Pattern: dd/MM/yyyy or dd-MM-yyyy (full date)
        Pattern fullDatePattern = Pattern.compile("(\\d{1,2})[/\\-](\\d{1,2})[/\\-](\\d{4})");
        Matcher fullMatcher = fullDatePattern.matcher(message);
        if (fullMatcher.find()) {
            try {
                int day = Integer.parseInt(fullMatcher.group(1));
                int month = Integer.parseInt(fullMatcher.group(2));
                int year = Integer.parseInt(fullMatcher.group(3));
                return LocalDate.of(year, month, day);
            } catch (Exception ignored) {
            }
        }

        // Pattern: dd/MM or dd-MM (short date, assume current year)
        Pattern shortDatePattern = Pattern.compile("(\\d{1,2})[/\\-](\\d{1,2})(?![/\\-\\d])");
        Matcher shortMatcher = shortDatePattern.matcher(message);
        if (shortMatcher.find()) {
            try {
                int day = Integer.parseInt(shortMatcher.group(1));
                int month = Integer.parseInt(shortMatcher.group(2));
                int year = LocalDate.now().getYear();
                // If the resulting date is more than 6 months in the past, assume next year
                LocalDate candidate = LocalDate.of(year, month, day);
                if (candidate.isBefore(LocalDate.now().minusMonths(6))) {
                    candidate = LocalDate.of(year + 1, month, day);
                }
                return candidate;
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    /**
     * Build a response listing all movies and their showtimes for a specific date.
     */
    private String buildDateShowtimesResponse(LocalDate date, Locale locale) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        List<Schedule> schedules = scheduleRepo.findActiveSchedulesOverlapping(startOfDay, endOfDay)
                .stream()
                .filter(s -> s.getMovie() != null)
                .sorted(Comparator.comparing(Schedule::getStartAt))
                .toList();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String dateLabel = date.format(dateFormatter);

        if (schedules.isEmpty()) {
            return message(locale, "chatbot.movie.date_no_showtimes", dateLabel);
        }

        // Group showtimes by movie
        Map<Long, Movie> movieMap = new java.util.LinkedHashMap<>();
        Map<Long, List<Schedule>> schedulesByMovie = new java.util.LinkedHashMap<>();
        for (Schedule s : schedules) {
            Movie movie = s.getMovie();
            movieMap.put(movie.getId(), movie);
            schedulesByMovie.computeIfAbsent(movie.getId(), k -> new ArrayList<>()).add(s);
        }

        StringBuilder response = new StringBuilder();
        response.append(message(locale, "chatbot.movie.date_showtimes_header", dateLabel)).append("\n");

        for (Map.Entry<Long, Movie> entry : movieMap.entrySet()) {
            Movie movie = entry.getValue();
            List<Schedule> movieSchedules = schedulesByMovie.get(entry.getKey());
            String movieName = getLocalizedMovieName(movie, locale);
            String genres = formatMovieTypes(movie, locale);

            response.append(movieName).append(genres).append("\n");
            response.append(message(locale, "chatbot.movie.date_label")).append(dateLabel).append("\n");
            response.append(message(locale, "chatbot.movie.showtimes_label"));

            String times = movieSchedules.stream()
                    .map(s -> {
                        String time = s.getStartAt() != null ? s.getStartAt().format(timeFormatter) : "";
                        String room = s.getRoom() != null ? s.getRoom().getName() : "";
                        String cinema = (s.getRoom() != null && s.getRoom().getCinema() != null)
                                ? s.getRoom().getCinema().getNameOfCinema() : "";
                        if (!cinema.isBlank() && !room.isBlank()) {
                            return time + " (" + cinema + " - " + room + ")";
                        } else if (!room.isBlank()) {
                            return time + " (" + room + ")";
                        }
                        return time;
                    })
                    .collect(Collectors.joining(", "));
            response.append(times).append("\n");
        }

        return response.toString().trim();
    }

    private String message(Locale locale, String key, Object... args) {
        return messageSource.getMessage(key, args, locale);
    }

    private boolean wantsPromotions(String message) {
        return containsAny(message, "khuyến mãi", "khuyen mai", "ưu đãi", "uu dai", "giảm giá", "giam gia", "sale", "promo", "discount", "promotion");
    }

    private boolean wantsMovieRelated(String message) {
        return containsAny(message, "phim gì", "co phim gi", "có phim gì", "goi y phim", "gợi ý phim", "de xuat phim",
                "đề xuất phim", "phim de xem", "phim để xem", "xem phim", "phim nao", "phim nào", "the loai", "thể loại",
                "phim", "movie", "đang chiếu", "sắp chiếu", "suất chiếu", "lịch chiếu", "what movie", "movie list", "recommend");
    }

    private boolean wantsRecommendation(String message) {
        return containsAny(message, "phù hợp với tôi", "phu hop voi toi", "gợi ý cho tôi", "goi y cho toi",
                "đề xuất cho tôi", "de xuat cho toi", "nên xem", "nen xem", "cho tôi phim", "chon phim", "phim hợp với tôi",
                "phim phu hop voi toi", "recommend me", "suggest me", "for me");
    }

    private boolean isUpcomingOnly(List<Movie> movies) {
        return movies != null && !movies.isEmpty() && movies.stream().allMatch(movie -> movie != null
                && movie.getPremiereDate() != null && movie.getPremiereDate().isAfter(LocalDateTime.now()));
    }

    private boolean isEnglish(Locale locale) {
        return locale != null && EN_LOCALE.getLanguage().equalsIgnoreCase(locale.getLanguage());
    }

    private String getLocalizedMovieName(Movie movie, Locale locale) {
        if (movie == null) {
            return "";
        }
        if (isEnglish(locale) && movie.getNameEn() != null && !movie.getNameEn().isBlank()) {
            return movie.getNameEn();
        }
        return movie.getName() != null ? movie.getName() : "";
    }

    private String getLocalizedDescription(Movie movie, Locale locale) {
        if (movie == null) {
            return "";
        }
        if (isEnglish(locale) && movie.getDescriptionEn() != null && !movie.getDescriptionEn().isBlank()) {
            return movie.getDescriptionEn().trim();
        }
        return movie.getDescription() != null ? movie.getDescription().trim() : "";
    }

    private String getLocalizedMovieTypeName(MovieType movieType, Locale locale) {
        if (movieType == null) {
            return "";
        }
        if (isEnglish(locale) && movieType.getMovieTypeNameEn() != null && !movieType.getMovieTypeNameEn().isBlank()) {
            return movieType.getMovieTypeNameEn();
        }
        return movieType.getMovieTypeNameVi() != null ? movieType.getMovieTypeNameVi() : "";
    }

    private boolean matchesLocalizedText(String normalizedQuestion, String normalizedCandidate) {
        return normalizedCandidate != null && !normalizedCandidate.isBlank()
                && (normalizedQuestion.contains(normalizedCandidate) || normalizedCandidate.contains(normalizedQuestion));
    }

    private String normalizeText(String input) {
        if (input == null) {
            return "";
        }
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return normalized.toLowerCase(Locale.ROOT);
    }

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
            - For movie advice or movie detail answers, focus on the movie's plot, genre, and suitability for the user.
            - Do not mention production credits such as director, cast, screenplay, producer, or studio unless the user explicitly asks for them.
            - Do not turn a movie into a list of facts; write it as a short advisory answer.
        """.formatted(context, question);
    }

    private String buildMovieDetailContext(Movie movie, Locale locale) {
        if (movie == null || getLocalizedMovieName(movie, locale).isBlank()) {
            return "";
        }

        String genres = movie.getMovieTypes() == null ? "" : movie.getMovieTypes().stream()
                .map(movieType -> getLocalizedMovieTypeName(movieType, locale))
                .filter(genre -> genre != null && !genre.isBlank())
                .distinct()
                .limit(3)
                .collect(Collectors.joining(", "));
        String description = getLocalizedDescription(movie, locale);

        return """
            Movie data:
            - Title: %s
            - Genres: %s
            - Original description: %s

            Answer requirements:
            - Rewrite only the information provided above into a short movie-advice response.
            - Use the original description as the only source for plot or context details.
            - Do not infer sequel, reboot, franchise, universe, or part-number relationships from the title or from external knowledge.
            - If the original description explicitly says reboot, you may mention reboot; otherwise omit any series or part relationship.
            - Never mention director, cast, screenplay, producer, or studio even if that information appears in the description.
            - Do not mention the release date.
            - Do not invent any details that are not in the data.
            - If the description is missing, give a very short introduction based only on the title and genres.
        """.formatted(getLocalizedMovieName(movie, locale), genres, description);
    }

    // ─── Ticket price ──────────────────────────────────────────────────────────

    private boolean wantsTicketPrice(String message) {
        return containsAny(message,
                "giá vé", "gia ve", "vé giá bao nhiêu", "ve gia bao nhieu",
                "giá xem phim", "gia xem phim", "bao nhiêu tiền", "bao nhieu tien",
                "chi phí", "chi phi", "phí vé", "phi ve",
                "ticket price", "how much", "price", "cost");
    }

    private boolean wantsCinemaInfo(String message) {
        return containsAny(message,
                "địa chỉ", "dia chi", "rạp ở đâu", "rap o dau", "rạp phim ở đâu",
                "vị trí rạp", "vi tri rap", "rạp cinema", "rạp có ở đâu",
                "rạp nào", "rap nao", "danh sách rạp", "danh sach rap",
                "cinema address", "where is the cinema", "cinema location", "location");
    }

    /**
     * Build a clear ticket price table from enum definitions and GeneralSetting.
     */
    private String buildTicketPriceResponse(Locale locale) {
        StringBuilder sb = new StringBuilder();
        sb.append(message(locale, "chatbot.price.header")).append("\n\n");

        // Base prices by seat type
        sb.append(message(locale, "chatbot.price.seat_type_header")).append("\n");
        for (SeatTypeEnum type : SeatTypeEnum.values()) {
            sb.append("  - ").append(type.getName())
              .append(": ").append(String.format("%,d", (long) type.getPrice()))
              .append(" VND\n");
        }

        // Room multipliers
        sb.append("\n").append(message(locale, "chatbot.price.room_type_header")).append("\n");
        for (RoomTypeEnum room : RoomTypeEnum.values()) {
            long exampleBase = (long) SeatTypeEnum.STANDARD.getPrice();
            long examplePrice = Math.round(exampleBase * room.getPriceMultiplier());
            sb.append("  - ").append(room.name())
              .append(" (x").append(room.getPriceMultiplier()).append(")")
              .append(message(locale, "chatbot.price.room_example"))
              .append(String.format("%,d", examplePrice)).append(" VND\n");
        }

        // Weekend surcharge
        generalSettingRepo.findTopByOrderByIdDesc().ifPresent(setting -> {
            if (setting.getPercentWeekend() != null && setting.getPercentWeekend() > 0) {
                sb.append("\n").append(message(locale, "chatbot.price.weekend_surcharge",
                        setting.getPercentWeekend())).append("\n");
            }
        });

        // Time-of-day discounts
        sb.append("\n").append(message(locale, "chatbot.price.time_discount_header")).append("\n");
        sb.append(message(locale, "chatbot.price.time_discounts"));

        return sb.toString().trim();
    }

    /**
     * Build a response listing all active cinemas with name and address.
     * If a location keyword is provided, filters cinemas whose address contains that keyword.
     */
    private String buildCinemaInfoResponse(String location, Locale locale) {
        List<Cinema> cinemas;

        if (location != null && !location.isBlank()) {
            cinemas = cinemaRepo.findAllByAddressContainingIgnoreCaseAndIsActiveTrueOrderByNameOfCinemaAsc(location);
            if (cinemas.isEmpty()) {
                // Fallback: show all cinemas with a note
                cinemas = cinemaRepo.findAllByIsActiveTrueOrderByIdAsc();
                if (cinemas.isEmpty()) {
                    return message(locale, "chatbot.cinema.none");
                }
                StringBuilder sb = new StringBuilder();
                sb.append(message(locale, "chatbot.cinema.none_in_area", location)).append("\n\n");
                sb.append(message(locale, "chatbot.cinema.header")).append("\n");
                appendCinemaList(sb, cinemas, locale);
                return sb.toString().trim();
            }

            StringBuilder sb = new StringBuilder();
            sb.append(message(locale, "chatbot.cinema.location_header", location)).append("\n\n");
            appendCinemaList(sb, cinemas, locale);
            return sb.toString().trim();
        }

        cinemas = cinemaRepo.findAllByIsActiveTrueOrderByIdAsc();
        if (cinemas.isEmpty()) {
            return message(locale, "chatbot.cinema.none");
        }

        StringBuilder sb = new StringBuilder();
        sb.append(message(locale, "chatbot.cinema.header")).append("\n");
        appendCinemaList(sb, cinemas, locale);
        return sb.toString().trim();
    }

    private void appendCinemaList(StringBuilder sb, List<Cinema> cinemas, Locale locale) {
        for (int i = 0; i < cinemas.size(); i++) {
            Cinema cinema = cinemas.get(i);
            sb.append(i + 1).append(". ").append(cinema.getNameOfCinema()).append("\n");
            // Show description as the detailed address (address field stores area/city only)
            if (cinema.getDescription() != null && !cinema.getDescription().isBlank()) {
                sb.append("   ").append(message(locale, "chatbot.cinema.address_label"))
                  .append(" ").append(cinema.getDescription()).append("\n");
            }
        }
    }

    /**
     * Extract a location/area keyword from the user message.
     * Looks for common Vietnamese city/district patterns.
     */
    private String extractLocationFromMessage(String message) {
        // Patterns: "tại X", "ở X", "khu vực X", "tại khu X"
        String normalized = message.toLowerCase(Locale.ROOT);
        String[] locationPrefixes = {"tại khu vực ", "khu vực ", "tại ", "ở ", "in ", "at ", "near "};
        for (String prefix : locationPrefixes) {
            int idx = normalized.indexOf(prefix);
            if (idx >= 0) {
                String candidate = message.substring(idx + prefix.length()).trim();
                // Cut off at punctuation or end of meaningful word(s)
                candidate = candidate.replaceAll("[?!.,;]+$", "").trim();
                // Limit to first 3 words to avoid capturing too much
                String[] words = candidate.split("\\s+");
                int take = Math.min(words.length, 3);
                String location = String.join(" ", java.util.Arrays.copyOf(words, take)).trim();
                if (!location.isBlank()) {
                    return location;
                }
            }
        }
        return null;
    }
}
