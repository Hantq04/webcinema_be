package vi.wbca.webcinema.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.mapper.FoodMapper;
import vi.wbca.webcinema.model.dto.cinema.FoodDTO;
import vi.wbca.webcinema.model.entity.cinema.Food;
import vi.wbca.webcinema.model.request.FoodRequest;
import vi.wbca.webcinema.repository.cinema.FoodRepo;
import vi.wbca.webcinema.service.FoodService;
import vi.wbca.webcinema.util.ImageUtils;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodServiceImpl implements FoodService {
    private final FoodRepo foodRepo;
    private final FoodMapper foodMapper;

    @Override
    public FoodDTO insertFood(FoodRequest foodRequest) throws IOException {
        String imageUrl = ImageUtils.saveImage(foodRequest.getFile());

        Food food = new Food();
        food.setPrice(foodRequest.getPrice());
        food.setDescription(foodRequest.getDescription());
        food.setImage(imageUrl);
        food.setNameOfFood(foodRequest.getNameOfFood());
        food.setActive(true);
        food = foodRepo.save(food);
        return foodMapper.toFoodDTO(food);
    }

    @Override
    public void updateFood(FoodRequest foodRequest) throws IOException {
        Food food = foodRepo.findByNameOfFood(foodRequest.getNameOfFood())
                .orElseThrow(() -> new AppException(ErrorCode.NAME_NOT_FOUND));
        String imageUrl = ImageUtils.saveImage(foodRequest.getFile());
        food.setPrice(foodRequest.getPrice());
        food.setDescription(foodRequest.getDescription());
        food.setImage(imageUrl);
        food.setNameOfFood(foodRequest.getNameOfFood());
        foodRepo.save(food);
    }

    @Override
    public void deleteFood(String name) {
        Food food = foodRepo.findByNameOfFood(name)
                .orElseThrow(() -> new AppException(ErrorCode.NAME_NOT_FOUND));
        food.setActive(false);
        foodRepo.save(food);
    }

    @Override
    public List<FoodDTO> getAllFoodActive() {
        return foodRepo.findAllByIsActiveTrueOrderByPriceDesc().stream()
                .map(foodMapper::toFoodDTO)
                .toList();
    }
}
