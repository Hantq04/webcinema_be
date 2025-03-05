package vi.wbca.webcinema.service.refreshTokenService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.dto.TokenDTO;
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

    @Value("${application.jwt.refresh-token.expiration}")
    private long expiredTime;

    @Override
    public void insertRefreshToken(User user) {
        // Check if the token exists; if it does, update it; otherwise, insert a new one
        RefreshToken refreshToken = refreshTokenRepo.findByUser(user)
                .orElse(new RefreshToken());
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiredTime(new Date(System.currentTimeMillis() + expiredTime));
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
        RefreshToken refreshToken = refreshTokenRepo.findByToken(token)
                .orElseThrow(() -> new AppException(ErrorCode.TOKEN_NOT_FOUND));
        AccessToken accessToken = accessTokenRepo.findByUser(refreshToken.getUser())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Check access token expired time
        if (accessToken.getExpiredAt().before(new Date()) && accessToken.getTokenStatus().equals(TokenStatus.EXPIRED)) {
            String newAccessToken = jwtTokenProvider.generateToken(accessToken.getUser());
            accessToken.setAccessToken(newAccessToken);
        } else throw new AppException(ErrorCode.TOKEN_STILL_VALID);

        // Check refresh token expired time
        if (refreshToken.getExpiredTime().before(new Date())) {
            refreshToken.setToken(UUID.randomUUID().toString());
            refreshToken.setExpiredTime(new Date(System.currentTimeMillis() + expiredTime));
        } else throw new AppException(ErrorCode.TOKEN_STILL_VALID);

        accessTokenRepo.save(accessToken);
        refreshTokenRepo.save(refreshToken);
        return new TokenDTO(accessToken.getAccessToken(), refreshToken.getToken());
    }
}
