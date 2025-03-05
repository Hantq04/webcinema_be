package vi.wbca.webcinema.service.foodService;

import vi.wbca.webcinema.dto.FoodDTO;
import vi.wbca.webcinema.dto.UserDTO;

public interface FoodService {
    FoodDTO insertFood(FoodDTO foodDTO);
    void updateFood(FoodDTO foodDTO);
    void deleteFood(String name);
}
