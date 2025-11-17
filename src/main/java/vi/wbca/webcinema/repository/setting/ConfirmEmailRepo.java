package vi.wbca.webcinema.repository.setting;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.setting.ConfirmEmail;

import java.util.Optional;

@Repository
public interface ConfirmEmailRepo extends JpaRepository<ConfirmEmail, Long> {
    Optional<ConfirmEmail> findByConfirmCode(String token);
}
