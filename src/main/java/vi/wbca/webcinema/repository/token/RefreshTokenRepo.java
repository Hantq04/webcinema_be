package vi.wbca.webcinema.repository.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.token.RefreshToken;
import vi.wbca.webcinema.model.user.User;

import java.util.Optional;

@Repository
public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findFirstByToken(String token);

    Optional<RefreshToken> findByUser(User user);
}
