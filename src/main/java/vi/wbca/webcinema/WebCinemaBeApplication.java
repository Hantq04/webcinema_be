package vi.wbca.webcinema;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WebCinemaBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebCinemaBeApplication.class, args);
    }

}
