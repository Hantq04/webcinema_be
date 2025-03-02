package vi.wbca.webcinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.UserStatus;

import java.util.Optional;

@Repository
public interface UserStatusRepo extends JpaRepository<UserStatus, Long> {
    UserStatus findByCode(String code);
}
