package vi.wbca.webcinema.service;

import vi.wbca.webcinema.model.dto.cinema.FoodDTO;

public interface FoodService {
    FoodDTO insertFood(FoodDTO foodDTO);
    void updateFood(FoodDTO foodDTO);
    void deleteFood(String name);
}
