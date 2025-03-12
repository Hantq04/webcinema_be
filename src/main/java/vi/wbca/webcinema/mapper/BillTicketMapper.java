package vi.wbca.webcinema.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vi.wbca.webcinema.dto.BillTicketDTO;
import vi.wbca.webcinema.model.BillTicket;

@Mapper(componentModel = "spring")
public interface BillTicketMapper {
    @Mapping(target = "id", ignore = true)
    BillTicket toBillTicket(BillTicketDTO billTicketDTO);

    BillTicketDTO toBillTicketDTO(BillTicket billTicket);
}
