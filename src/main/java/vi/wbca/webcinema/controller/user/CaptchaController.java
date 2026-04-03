package vi.wbca.webcinema.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vi.wbca.webcinema.model.response.CaptchaResponse;
import vi.wbca.webcinema.service.CaptchaService;

@RestController
@RequestMapping("/api/v1/captcha")
@RequiredArgsConstructor
public class CaptchaController {

    private final CaptchaService captchaService;

    @GetMapping
    @Operation(summary = "Tạo ảnh captcha")
    public CaptchaResponse getCaptcha() {
        return captchaService.generateCaptcha();
    }
}
