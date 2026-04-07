package vi.wbca.webcinema.chatbot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import vi.wbca.webcinema.chatbot.service.ChatBotService;
import vi.wbca.webcinema.chatbot.model.request.ChatRequest;
import vi.wbca.webcinema.chatbot.model.response.ChatResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat-bot")
public class ChatBotController {
    private final ChatBotService chatBotService;

    @PostMapping("/ask")
    public ResponseEntity<ChatResponse> ask(@RequestBody ChatRequest request) {
        return ResponseEntity.ok(chatBotService.chat(request));
    }
}
