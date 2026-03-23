package vi.wbca.webcinema.service;

import vi.wbca.webcinema.model.entity.token.AccessToken;
import vi.wbca.webcinema.model.entity.user.User;

public interface AccessTokenService {
    void insertAccessToken(User user, String token);

    AccessToken save(AccessToken accessToken);

    void deleteAccessToken(AccessToken accessToken);

    AccessToken findByAccessToken(String token);

    void revokeAllUserTokens(User user);
}
