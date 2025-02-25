package vi.wbca.webcinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.ConfirmEmail;

@Repository
public interface ConfirmEmailRepo extends JpaRepository<ConfirmEmail, Long> {
}
