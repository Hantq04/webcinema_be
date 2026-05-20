package vi.wbca.webcinema.repository.notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.entity.notification.Notification;
import vi.wbca.webcinema.model.entity.user.User;

import java.util.Optional;

@Repository
public interface NotificationRepo extends JpaRepository<Notification, Long> {
    Page<Notification> findAllByUserOrderByCreateTimeDesc(User user, Pageable pageable);

    long countByUserAndIsReadFalse(User user);

    Optional<Notification> findByIdAndUser(Long id, User user);
}