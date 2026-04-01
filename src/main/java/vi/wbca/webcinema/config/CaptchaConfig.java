package vi.wbca.webcinema.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class CaptchaConfig {

    @Bean
    public DefaultKaptcha captchaProducer() {
        Properties props = new Properties();

        props.put("kaptcha.image.width", "160");
        props.put("kaptcha.image.height", "60");

        props.put("kaptcha.textproducer.char.string", "ABCDEFGHJKLMNPQRSTUVWXYZ23456789");
        props.put("kaptcha.textproducer.char.length", "6");

        props.put("kaptcha.textproducer.font.size", "40");
        props.put("kaptcha.noise.impl", "com.google.code.kaptcha.impl.DefaultNoise");
        props.put("kaptcha.obscurificator.impl", "com.google.code.kaptcha.impl.WaterRipple");

        Config config = new Config(props);

        DefaultKaptcha captcha = new DefaultKaptcha();
        captcha.setConfig(config);

        return captcha;
    }
}
