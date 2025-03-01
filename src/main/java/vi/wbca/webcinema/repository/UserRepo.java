package vi.wbca.webcinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.ConfirmEmail;
import vi.wbca.webcinema.model.User;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUserName(String userName);
    Optional<User> findByConfirmEmails(ConfirmEmail confirmEmails);

    boolean existsByEmail(String email);
    boolean existsByUserName(String userName);
    boolean existsByPhoneNumber(String phoneNumber);
}
