package vi.wbca.webcinema.repository.cinema;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.cinema.Food;

import java.util.Optional;

@Repository
public interface FoodRepo extends JpaRepository<Food, Long> {
    Optional<Food> findByNameOfFood(String name);
}
