package vi.wbca.webcinema.repository.bill;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.entity.bill.UserPromotion;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPromotionRepo extends JpaRepository<UserPromotion, Long> {
    List<UserPromotion> findByUserId(Long userId);

    Optional<UserPromotion> findByUserIdAndPromotionId(Long userId, Long promotionId);
    
    List<UserPromotion> findByUserIdAndIsUsedFalse(Long userId);

    List<UserPromotion> findByUserIdAndPromotionEndTimeBefore(Long userId, java.time.LocalDateTime endTime);
}
