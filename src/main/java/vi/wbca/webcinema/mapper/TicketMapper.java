package vi.wbca.webcinema.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vi.wbca.webcinema.dto.ticket.TicketDTO;
import vi.wbca.webcinema.model.Ticket;

@Mapper(componentModel = "spring")
public interface TicketMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "billTickets", ignore = true)
    Ticket toTicket(TicketDTO ticketDTO);

    TicketDTO toTicketDTO(Ticket ticket);
}
