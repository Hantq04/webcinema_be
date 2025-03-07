package vi.wbca.webcinema.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vi.wbca.webcinema.dto.BillDTO;
import vi.wbca.webcinema.model.Bill;

@Mapper(componentModel = "spring")
public interface BillMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "billFoods", ignore = true)
    Bill toBill(BillDTO billDTO);

    BillDTO toBillDTO(Bill bill);
}
