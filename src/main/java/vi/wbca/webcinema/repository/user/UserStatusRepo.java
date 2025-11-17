package vi.wbca.webcinema.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.user.UserStatus;

@Repository
public interface UserStatusRepo extends JpaRepository<UserStatus, Long> {
    UserStatus findByCode(String code);
}
