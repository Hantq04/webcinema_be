package vi.wbca.webcinema.chatbot.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.chatbot.ai.GroqClient;
import vi.wbca.webcinema.chatbot.model.MovieFilter;
import vi.wbca.webcinema.chatbot.model.request.ChatRequest;
import vi.wbca.webcinema.chatbot.model.response.ChatResponse;
import vi.wbca.webcinema.chatbot.service.ChatBotService;
import vi.wbca.webcinema.model.entity.movie.Movie;
import vi.wbca.webcinema.model.entity.movie.MovieType;
import vi.wbca.webcinema.repository.movie.MovieRepo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatBotServiceImpl implements ChatBotService {

    private final MovieRepo movieRepo;
    private final GroqClient groqClient;

    @Override
    public ChatResponse chat(ChatRequest request) {

        String message = request.getMessage().toLowerCase();

        // 1. Detect intent
        MovieFilter filter = detectIntent(message);

        // 2. Query DB
        List<Movie> movies = getMovies(filter);

        // 3. Build context
        String context = buildContext(movies);

        // 4. Call AI (format only)
        String result = groqClient.ask(buildPrompt(context, message));

        return new ChatResponse(result);
    }

    // ================= INTENT =================

    private MovieFilter detectIntent(String message) {
        MovieFilter f = new MovieFilter();

        if (message.contains("đang chiếu")) f.setNowShowing(true);
        if (message.contains("sắp chiếu")) f.setComingSoon(true);

        if (message.contains("kinh dị")) f.setGenre("Kinh dị");
        if (message.contains("hành động")) f.setGenre("Hành động");
        if (message.contains("tình cảm")) f.setGenre("Tình cảm");

        // fallback: nếu không rõ → mặc định đang chiếu
        if (!f.isNowShowing() && !f.isComingSoon()) {
            f.setNowShowing(true);
        }

        return f;
    }

    // ================= DB QUERY =================

    private List<Movie> getMovies(MovieFilter f) {
        return movieRepo.filterMovies(
                LocalDateTime.now(),
                f.isNowShowing(),
                f.isComingSoon(),
                f.getGenre()
        ).stream().limit(5).toList();
    }

    // ================= CONTEXT =================

    private String buildContext(List<Movie> movies) {

        if (movies.isEmpty()) {
            return "Không có phim phù hợp.";
        }

        StringBuilder sb = new StringBuilder("Danh sách phim:\n");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDateTime now = LocalDateTime.now();

        for (Movie m : movies) {

            String status = getStatus(m, now);

            sb.append("- ")
                    .append(m.getName())
                    .append(" | ")
                    .append(m.getMovieTypes() == null ? "" : m.getMovieTypes().stream()
                        .map(MovieType::getMovieTypeNameVi)
                        .filter(name -> name != null && !name.isBlank())
                        .collect(Collectors.joining(", ")))
                    .append(" | ")
                    .append(m.getPremiereDate().format(formatter))
                    .append(" | ")
                    .append(status)
                    .append("\n");
        }

        return sb.toString();
    }

    private String getStatus(Movie m, LocalDateTime now) {
        if (!m.isActive()) return "Ngừng chiếu";

        if (m.getPremiereDate() != null && m.getPremiereDate().isAfter(now)) {
            return "Sắp chiếu";
        }

        if (m.getEndDate() == null || !m.getEndDate().isBefore(now)) {
            return "Đang chiếu";
        }

        return "Ngừng chiếu";
    }

    // ================= PROMPT =================

    private String buildPrompt(String context, String question) {
        return """
            Bạn là chatbot tư vấn phim cho rạp chiếu phim.
            Chỉ sử dụng dữ liệu dưới đây, không tự bịa: %s
            Câu hỏi: %s
            Yêu cầu:
            - Trả lời tự nhiên, thân thiện
            - Có thể xuống dòng cho dễ đọc
            - Không nói "dựa trên dữ liệu cung cấp"
        """.formatted(context, question);
    }
}
