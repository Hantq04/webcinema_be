package vi.wbca.webcinema.service.refreshTokenService;

import vi.wbca.webcinema.dto.token.TokenDTO;
import vi.wbca.webcinema.model.User;

public interface RefreshTokenService {
    void insertRefreshToken(User user);

    String getRefreshToken(User user);

    TokenDTO refreshToken(String token);
}
