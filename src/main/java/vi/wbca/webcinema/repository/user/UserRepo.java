package vi.wbca.webcinema.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.entity.setting.ConfirmEmail;
import vi.wbca.webcinema.model.entity.user.User;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String userName);

    Optional<User> findByConfirmEmails(ConfirmEmail confirmEmails);

    boolean existsByUserName(String userName);
}
