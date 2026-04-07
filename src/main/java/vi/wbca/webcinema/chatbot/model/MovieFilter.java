package vi.wbca.webcinema.chatbot.model;

import lombok.Data;

@Data
public class MovieFilter {
    private boolean nowShowing;
    private boolean comingSoon;
    private String genre;
}
