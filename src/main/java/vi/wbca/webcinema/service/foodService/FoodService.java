package vi.wbca.webcinema.service.foodService;

import vi.wbca.webcinema.dto.cinema.FoodDTO;

public interface FoodService {
    FoodDTO insertFood(FoodDTO foodDTO);
    void updateFood(FoodDTO foodDTO);
    void deleteFood(String name);
}
