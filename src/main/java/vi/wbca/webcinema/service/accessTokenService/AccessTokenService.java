package vi.wbca.webcinema.service.accessTokenService;

import vi.wbca.webcinema.model.AccessToken;
import vi.wbca.webcinema.model.User;

public interface AccessTokenService {
    AccessToken insertAccessToken(User user, String token);

    void deleteAccessToken(AccessToken accessToken);
}
