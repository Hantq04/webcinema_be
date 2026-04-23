package vi.wbca.webcinema.service;

import vi.wbca.webcinema.model.dto.cinema.FoodDTO;
import vi.wbca.webcinema.model.request.FoodRequest;

import java.io.IOException;
import java.util.List;

public interface FoodService {
    FoodDTO insertFood(FoodRequest foodRequest) throws IOException;

    void updateFood(FoodRequest foodRequest) throws IOException;

    void deleteFood(String name);

    List<FoodDTO> getAllFoodActive();
}
