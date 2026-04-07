package vi.wbca.webcinema.chatbot.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MemoryService {

    private final Map<Long, String> memory = new ConcurrentHashMap<>();

    public void save(Long userId, String message) {
        memory.put(userId, message);
    }

    public String get(Long userId) {
        return memory.getOrDefault(userId, "");
    }
}
