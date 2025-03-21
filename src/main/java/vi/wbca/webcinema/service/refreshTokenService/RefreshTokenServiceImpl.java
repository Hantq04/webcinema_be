package vi.wbca.webcinema.service.refreshTokenService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.dto.token.TokenDTO;
import vi.wbca.webcinema.enums.TokenStatus;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.model.AccessToken;
import vi.wbca.webcinema.model.RefreshToken;
import vi.wbca.webcinema.model.User;
import vi.wbca.webcinema.repository.AccessTokenRepo;
import vi.wbca.webcinema.repository.RefreshTokenRepo;
import vi.wbca.webcinema.util.jwt.JwtTokenProvider;

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService{
    private final RefreshTokenRepo refreshTokenRepo;
    private final AccessTokenRepo accessTokenRepo;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${application.jwt.expiration}")
    private long expiredTime;

    @Value("${application.jwt.refresh-token.expiration}")
    private long refTokenExpiredTime;

    @Override
    public void insertRefreshToken(User user) {
        // Check if the token exists; if it does, update it; otherwise, insert a new one
        RefreshToken refreshToken = refreshTokenRepo.findByUser(user)
                .orElse(new RefreshToken());

        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiredTime(new Date(System.currentTimeMillis() + refTokenExpiredTime));
        refreshToken.setUser(user);
        refreshTokenRepo.save(refreshToken);
    }

    @Override
    public String getRefreshToken(User user) {
        return refreshTokenRepo.findByUser(user)
                .map(RefreshToken::getToken).orElse(null);
    }

    @Override
    public TokenDTO refreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepo.findFirstByToken(token)
                .orElseThrow(() -> new AppException(ErrorCode.TOKEN_NOT_FOUND));
        AccessToken accessToken = accessTokenRepo.findLatestByUser(refreshToken.getUser().getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (refreshToken.getExpiredTime().before(new Date())) {
            throw new AppException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }
        TokenDTO response = new TokenDTO();
        response.setAccessToken(accessToken.getAccessToken());
        response.setRefreshToken(refreshToken.getToken());

        boolean isExpired = accessToken.getExpiredAt().before(new Date());

        if (isExpired) {
            String newAccessToken = jwtTokenProvider.generateToken(accessToken.getUser());
            accessToken.setTokenStatus(TokenStatus.ACTIVE);
            accessToken.setAccessToken(newAccessToken);
            accessToken.setExpiredAt(new Date(System.currentTimeMillis() + expiredTime));
            accessTokenRepo.save(accessToken);
            response.setAccessToken(newAccessToken);
        }
        response.setNewToken(isExpired);
        return response;
    }
}
