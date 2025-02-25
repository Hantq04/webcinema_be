package vi.wbca.webcinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.RefreshToken;

@Repository
public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long> {
}
