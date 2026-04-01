package vi.wbca.webcinema.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CaptchaResponse {
    private String captchaId;
    private String image;
}
