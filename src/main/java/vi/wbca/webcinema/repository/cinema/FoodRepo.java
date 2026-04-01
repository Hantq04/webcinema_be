package vi.wbca.webcinema.repository.cinema;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.entity.cinema.Food;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoodRepo extends JpaRepository<Food, Long> {
    Optional<Food> findByNameOfFood(String name);
    
    List<Food> findByIsActiveTrue();
}
