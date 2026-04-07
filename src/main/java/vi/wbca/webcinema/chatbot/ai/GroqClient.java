package vi.wbca.webcinema.chatbot.ai;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GroqClient {
    private static final String DEFAULT_MODEL = "llama-3.3-70b-versatile";

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.model:" + DEFAULT_MODEL + "}")
    private String model;

    private final WebClient webClient;

    public String ask(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException("Prompt must not be blank");
        }

        Map<String, Object> body = Map.of(
            "model", model,
            "messages", List.of(
            Map.of("role", "system", "content", "You are a helpful movie assistant for a cinema website."),
            Map.of("role", "user", "content", prompt)),
            "temperature", 0.2
        );

        return webClient.post()
            .uri("/chat/completions")
            .header("Authorization", "Bearer " + apiKey)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchangeToMono(response -> {
                 if (response.statusCode().isError()) {
                    return response.bodyToMono(String.class)
                    .defaultIfEmpty("")
                    .flatMap(errorBody -> Mono.error(new IllegalStateException(
                        "Groq request failed with status " + response.statusCode().value()
                        + (errorBody.isBlank() ? "" : ": " + errorBody)
                    )));
                 }

                 return response.bodyToMono(JsonNode.class);
                 })
                 .map(res -> {
                    JsonNode content = res.path("choices").path(0).path("message").path("content");
                    if (content.isMissingNode() || content.isNull() || content.asText().isBlank()) {
                        throw new IllegalStateException("Groq returned an empty response payload");
                    }
                    return content.asText();
                 })
            .block();
    }
}