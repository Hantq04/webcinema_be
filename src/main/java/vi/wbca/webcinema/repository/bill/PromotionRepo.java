package vi.wbca.webcinema.repository.bill;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.bill.Promotion;
import vi.wbca.webcinema.model.user.RankCustomer;

import java.util.Optional;

@Repository
public interface PromotionRepo extends JpaRepository<Promotion, Long> {
    Optional<Promotion> findByName(String name);

    Optional<Promotion> findByRankCustomer(RankCustomer rankCustomer);
}
