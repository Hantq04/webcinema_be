package vi.wbca.webcinema.service.refreshTokenService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.model.RefreshToken;
import vi.wbca.webcinema.model.User;
import vi.wbca.webcinema.repository.RefreshTokenRepo;
import vi.wbca.webcinema.repository.UserRepo;

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService{
    private final RefreshTokenRepo refreshTokenRepo;
    private final UserRepo userRepo;

    @Value("${application.jwt.refresh-token.expiration}")
    private long expiredTime;

    @Override
    public RefreshToken insertRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiredTime(new Date(System.currentTimeMillis() + expiredTime));
        refreshToken.setUser(user);
        return refreshTokenRepo.save(refreshToken);
    }

    @Override
    public RefreshToken refreshToken(String token) {
        return null;
    }
}
