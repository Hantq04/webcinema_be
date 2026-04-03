package vi.wbca.webcinema.repository.bill;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.entity.bill.Promotion;
import vi.wbca.webcinema.model.entity.user.RankCustomer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepo extends JpaRepository<Promotion, Long> {
    Optional<Promotion> findByName(String name);

    Optional<Promotion> findByCode(String code);

    List<Promotion> findByEndTimeBefore(LocalDateTime endTime);
}
