package vi.wbca.webcinema.repository.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.enums.TokenStatus;
import vi.wbca.webcinema.model.token.AccessToken;
import vi.wbca.webcinema.model.user.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccessTokenRepo extends JpaRepository<AccessToken, Long> {
    Optional<AccessToken> findByAccessToken(String token);

    List<AccessToken> findAllByUserAndTokenStatus(User user, TokenStatus tokenStatus);

    @Query(value = """
            SELECT * FROM access_tokens
            WHERE user_id = :userId
            AND token_status IN ('ACTIVE', 'EXPIRED')
            ORDER BY FIELD(token_status, 'ACTIVE', 'EXPIRED'), expired_at DESC
            LIMIT 1
            """, nativeQuery = true)
    Optional<AccessToken> findLatestByUser(@Param("userId") Long userId);
}
