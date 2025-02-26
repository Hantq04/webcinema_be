package vi.wbca.webcinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.Food;

@Repository
public interface FoodRepo extends JpaRepository<Food, Long> {
}
