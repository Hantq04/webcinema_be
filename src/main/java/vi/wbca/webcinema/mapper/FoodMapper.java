package vi.wbca.webcinema.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vi.wbca.webcinema.dto.cinema.FoodDTO;
import vi.wbca.webcinema.model.Food;

@Mapper(componentModel = "spring")
public interface FoodMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "billFoods", ignore = true)
    Food toFood(FoodDTO foodDTO);

    FoodDTO toFoodDTO(Food food);
}
