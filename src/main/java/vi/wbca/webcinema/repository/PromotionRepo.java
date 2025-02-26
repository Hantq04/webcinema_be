package vi.wbca.webcinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.Promotion;

@Repository
public interface PromotionRepo extends JpaRepository<Promotion, Long> {
}
