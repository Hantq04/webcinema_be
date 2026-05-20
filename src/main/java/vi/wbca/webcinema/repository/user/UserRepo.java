package vi.wbca.webcinema.repository.user;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("""
        select case when count(u) > 0 then true else false end
        from User u
        where u.userName = :userName
        and u.isActive = true
    """)
    boolean existsByUserNameAndIsActiveTrue(@Param("userName") String userName);
}
