package vi.wbca.webcinema.service;

import vi.wbca.webcinema.model.dto.cinema.FoodDTO;
import vi.wbca.webcinema.model.entity.cinema.Food;

import java.util.List;

public interface FoodService {
    FoodDTO insertFood(FoodDTO foodDTO);

    void updateFood(FoodDTO foodDTO);

    void deleteFood(String name);

    List<Food> getAllFoodActive();
}
