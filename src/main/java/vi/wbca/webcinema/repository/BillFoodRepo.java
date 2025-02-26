package vi.wbca.webcinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.BillFood;

@Repository
public interface BillFoodRepo extends JpaRepository<BillFood, Long> {
}
