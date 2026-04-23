package vi.wbca.webcinema.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import vi.wbca.webcinema.model.dto.cinema.FoodDTO;
import vi.wbca.webcinema.model.entity.cinema.Food;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FoodMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "billFoods", ignore = true)
    @Mapping(target = "active", ignore = true)
    Food toFood(FoodDTO foodDTO);

    FoodDTO toFoodDTO(Food food);
}
