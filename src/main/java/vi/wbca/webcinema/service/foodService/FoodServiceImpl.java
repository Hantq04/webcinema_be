package vi.wbca.webcinema.service.foodService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.dto.cinema.FoodDTO;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.mapper.FoodMapper;
import vi.wbca.webcinema.model.cinema.Food;
import vi.wbca.webcinema.repository.cinema.FoodRepo;

@Service
@RequiredArgsConstructor
public class FoodServiceImpl implements FoodService{
    private final FoodRepo foodRepo;
    private final FoodMapper foodMapper;

    @Override
    public FoodDTO insertFood(FoodDTO foodDTO) {
        Food food = foodMapper.toFood(foodDTO);
        food.setActive(true);
        foodRepo.save(food);
        return foodMapper.toFoodDTO(food);
    }

    @Override
    public void updateFood(FoodDTO foodDTO) {
        Food food = foodRepo.findByNameOfFood(foodDTO.getNameOfFood())
                .orElseThrow(() -> new AppException(ErrorCode.NAME_NOT_FOUND));
        food.setPrice(foodDTO.getPrice());
        food.setDescription(foodDTO.getDescription());
        food.setImage(foodDTO.getImage());
        food.setNameOfFood(foodDTO.getNameOfFood());
        foodRepo.save(food);
    }

    @Override
    public void deleteFood(String name) {
        Food food = foodRepo.findByNameOfFood(name)
                .orElseThrow(() -> new AppException(ErrorCode.NAME_NOT_FOUND));
        foodRepo.delete(food);
    }
}
