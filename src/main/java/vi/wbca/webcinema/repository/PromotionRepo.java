package vi.wbca.webcinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.Promotion;
import vi.wbca.webcinema.model.RankCustomer;

import java.util.Optional;

@Repository
public interface PromotionRepo extends JpaRepository<Promotion, Long> {
    Optional<Promotion> findByName(String name);

    Optional<Promotion> findByRankCustomer(RankCustomer rankCustomer);
}
