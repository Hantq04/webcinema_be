package vi.wbca.webcinema.service;

import vi.wbca.webcinema.model.token.AccessToken;
import vi.wbca.webcinema.model.user.User;

public interface AccessTokenService {
    void insertAccessToken(User user, String token);

    AccessToken save(AccessToken accessToken);

    void deleteAccessToken(AccessToken accessToken);

    AccessToken findByAccessToken(String token);

    void revokeAllUserTokens(User user);
}
