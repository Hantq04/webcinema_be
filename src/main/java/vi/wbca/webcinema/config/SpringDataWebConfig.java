package vi.wbca.webcinema.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class SpringDataWebConfig {
    // Enables Spring Data web support with DTO-based page serialization for clean pagination and sorting handling in web requests
}
