package vi.wbca.webcinema.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vi.wbca.webcinema.dto.bill.BillTicketDTO;
import vi.wbca.webcinema.model.bill.BillTicket;

@Mapper(componentModel = "spring")
public interface BillTicketMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bill", ignore = true)
    @Mapping(target = "ticket", ignore = true)
    BillTicket toBillTicket(BillTicketDTO billTicketDTO);

    @Mapping(target = "customerName", ignore = true)
    @Mapping(target = "code", ignore = true)
    BillTicketDTO toBillTicketDTO(BillTicket billTicket);
}
