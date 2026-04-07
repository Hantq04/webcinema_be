package vi.wbca.webcinema.chatbot.service;

import vi.wbca.webcinema.chatbot.model.request.ChatRequest;
import vi.wbca.webcinema.chatbot.model.response.ChatResponse;

public interface ChatBotService {
    ChatResponse chat(ChatRequest request);
}
