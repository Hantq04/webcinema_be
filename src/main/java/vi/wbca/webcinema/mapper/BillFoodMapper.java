package vi.wbca.webcinema.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vi.wbca.webcinema.dto.bill.BillFoodDTO;
import vi.wbca.webcinema.model.bill.BillFood;

@Mapper(componentModel = "spring")
public interface BillFoodMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bill", ignore = true)
    @Mapping(target = "food", ignore = true)
    BillFood toBillFood(BillFoodDTO billFoodDTO);

    @Mapping(target = "customerName", ignore = true)
    @Mapping(target = "name", ignore = true)
    BillFoodDTO toBillFoodDTO(BillFood billFood);
}
