package vi.wbca.webcinema.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.entity.user.ChangeTypeEnum;
import vi.wbca.webcinema.model.entity.user.User;
import vi.wbca.webcinema.model.entity.user.UserChangeHistory;

import java.time.LocalDateTime;

@Repository
public interface UserChangeHistoryRepo extends JpaRepository<UserChangeHistory, Long> {
    boolean existsByUserAndChangeTypeAndChangedAtAfter(User user, ChangeTypeEnum changeType, LocalDateTime changedAfter);
}
