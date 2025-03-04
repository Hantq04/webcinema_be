package vi.wbca.webcinema.service.accessTokenService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.enums.TokenStatus;
import vi.wbca.webcinema.model.AccessToken;
import vi.wbca.webcinema.model.User;
import vi.wbca.webcinema.repository.AccessTokenRepo;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AccessTokenServiceImpl implements AccessTokenService{
    private final AccessTokenRepo accessTokenRepo;

    @Value("${application.jwt.expiration}")
    private long expiredTime;

    @Override
    public AccessToken insertAccessToken(User user, String token) {
        AccessToken accessToken = new AccessToken();
        accessToken.setTokenStatus(TokenStatus.ACTIVE);
        accessToken.setAccessToken(token);
        accessToken.setUser(user);
        accessToken.setExpiredAt(new Date(System.currentTimeMillis() + expiredTime));
        return accessTokenRepo.save(accessToken);
    }

    @Override
    public void deleteAccessToken(AccessToken accessToken) {
        accessTokenRepo.delete(accessToken);
    }
}
