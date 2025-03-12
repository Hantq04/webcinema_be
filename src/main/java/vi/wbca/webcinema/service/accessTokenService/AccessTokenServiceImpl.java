package vi.wbca.webcinema.service.accessTokenService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.enums.TokenStatus;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.model.AccessToken;
import vi.wbca.webcinema.model.User;
import vi.wbca.webcinema.repository.AccessTokenRepo;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccessTokenServiceImpl implements AccessTokenService{
    private final AccessTokenRepo accessTokenRepo;

    @Value("${application.jwt.expiration}")
    private long expiredTime;

    @Override
    public void insertAccessToken(User user, String token) {
        AccessToken accessToken = new AccessToken();

        accessToken.setTokenStatus(TokenStatus.ACTIVE);
        accessToken.setAccessToken(token);
        accessToken.setUser(user);
        accessToken.setExpiredAt(new Date(System.currentTimeMillis() + expiredTime));
        accessTokenRepo.save(accessToken);
    }

    @Override
    public AccessToken save(AccessToken accessToken) {
        return accessTokenRepo.save(accessToken);
    }

    @Override
    public void deleteAccessToken(AccessToken accessToken) {
        accessTokenRepo.delete(accessToken);
    }

    @Override
    public AccessToken findByAccessToken(String token) {
        return accessTokenRepo.findByAccessToken(token)
                .orElseThrow(() -> new AppException(ErrorCode.TOKEN_NOT_FOUND));
    }

    @Override
    public void revokeAllUserTokens(User user) {
        List<AccessToken> validTokens = accessTokenRepo.findAllByUserAndTokenStatus(user, TokenStatus.ACTIVE);
        if (!validTokens.isEmpty()) {
            validTokens.forEach(token -> token.setTokenStatus(TokenStatus.REVOKED));
            accessTokenRepo.saveAll(validTokens);
        }
    }
}
