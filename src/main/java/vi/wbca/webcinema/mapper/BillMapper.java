package vi.wbca.webcinema.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vi.wbca.webcinema.dto.bill.BillDTO;
import vi.wbca.webcinema.model.Bill;

@Mapper(componentModel = "spring")
public interface BillMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "billFoods", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "promotion", ignore = true)
    @Mapping(target = "billStatus", ignore = true)
    Bill toBill(BillDTO billDTO);

    @Mapping(target = "customerName", ignore = true)
    @Mapping(target = "foods", ignore = true)
    @Mapping(target = "tickets", ignore = true)
    BillDTO toBillDTO(Bill bill);
}
