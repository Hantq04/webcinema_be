package vi.wbca.webcinema.service;

import vi.wbca.webcinema.model.dto.token.TokenDTO;
import vi.wbca.webcinema.model.entity.user.User;

public interface RefreshTokenService {
    void insertRefreshToken(User user);

    String getRefreshToken(User user);

    TokenDTO refreshToken(String token);
}
