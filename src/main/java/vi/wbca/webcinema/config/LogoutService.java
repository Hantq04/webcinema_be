package vi.wbca.webcinema.config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.repository.AccessTokenRepo;

import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class LogoutService {
    private static final Logger logger = Logger.getLogger(LogoutService.class.getName());
    private final AccessTokenRepo accessTokenRepo;
}
