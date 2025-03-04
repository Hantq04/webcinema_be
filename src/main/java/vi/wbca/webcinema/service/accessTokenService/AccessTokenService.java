package vi.wbca.webcinema.service.accessTokenService;

import vi.wbca.webcinema.model.AccessToken;
import vi.wbca.webcinema.model.User;

public interface AccessTokenService {
    void insertAccessToken(User user, String token);

    AccessToken save(AccessToken accessToken);

    void deleteAccessToken(AccessToken accessToken);

    AccessToken findByAccessToken(String token);
}
