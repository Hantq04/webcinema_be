package vi.wbca.webcinema.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vi.wbca.webcinema.dto.bill.BillFoodDTO;
import vi.wbca.webcinema.model.BillFood;

@Mapper(componentModel = "spring")
public interface BillFoodMapper {
    @Mapping(target = "id", ignore = true)
    BillFood toBillFood(BillFoodDTO billFoodDTO);

    BillFoodDTO toBillFoodDTO(BillFood billFood);
}
