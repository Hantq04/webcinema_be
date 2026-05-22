package vi.wbca.webcinema.chatbot.model.request;

import lombok.*;

@Data
public class ChatRequest {
    private Long userId;
    private String message;
}
