package vi.wbca.webcinema.service.refreshTokenService;

import vi.wbca.webcinema.model.RefreshToken;
import vi.wbca.webcinema.model.User;

public interface RefreshTokenService {
    RefreshToken insertRefreshToken(User user);

    RefreshToken refreshToken(String token);
}
