package vi.wbca.webcinema.service;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.model.response.CaptchaResponse;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CaptchaService {

    private final DefaultKaptcha captchaProducer;
    private final StringRedisTemplate redisTemplate;
    private static final String PREFIX = "captcha:";
    private static final Duration TTL = Duration.ofMinutes(2);

    public CaptchaResponse generateCaptcha() {
        String text = captchaProducer.createText();
        BufferedImage image = captchaProducer.createImage(text);

        String captchaId = UUID.randomUUID().toString();

        redisTemplate.opsForValue().set(PREFIX + captchaId, text, TTL);

        String base64 = toBase64(image);

        return new CaptchaResponse(captchaId, "data:image/jpeg;base64," + base64);
    }

    public void validateCaptcha(String captchaId, String input) {
        String key = PREFIX + captchaId;
        String stored = redisTemplate.opsForValue().get(key);

        if (stored == null) {
            throw new AppException(ErrorCode.CAPTCHA_REFRESH);
        }
        if (!stored.equalsIgnoreCase(input.trim())) {
            redisTemplate.delete(key);
            throw new AppException(ErrorCode.INVALID_CAPTCHA);
        }
        redisTemplate.delete(key);
    }

    private String toBase64(BufferedImage image) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", baos);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Captcha convert error");
        }
    }
}
