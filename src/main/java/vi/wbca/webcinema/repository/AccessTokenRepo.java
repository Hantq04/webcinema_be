package vi.wbca.webcinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.enums.TokenStatus;
import vi.wbca.webcinema.model.AccessToken;
import vi.wbca.webcinema.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccessTokenRepo extends JpaRepository<AccessToken, Long> {
    Optional<AccessToken> findByAccessToken(String token);

    List<AccessToken> findAllByUserAndTokenStatus(User user, TokenStatus active);
}
