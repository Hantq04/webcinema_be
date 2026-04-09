package vi.wbca.webcinema.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vi.wbca.webcinema.model.dto.ticket.TicketDTO;
import vi.wbca.webcinema.model.entity.movie.Ticket;
import vi.wbca.webcinema.model.response.TicketResponse;

@Mapper(componentModel = "spring")
public interface TicketMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "billTickets", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "schedule", ignore = true)
    @Mapping(target = "seat", ignore = true)
    Ticket toTicket(TicketDTO ticketDTO);

    @Mapping(target = "roomName", ignore = true)
    @Mapping(target = "roomCode", ignore = true)
    @Mapping(target = "startTime", ignore = true)
    @Mapping(target = "selectSeat", ignore = true)
    TicketDTO toTicketDTO(Ticket ticket);

    @Mapping(source = "ticket.id", target = "id")
    @Mapping(source = "ticket.code", target = "code")
    @Mapping(source = "ticket.active", target = "isActive")
    @Mapping(source = "ticket.priceTicket", target = "priceTicket")
    TicketResponse toTicketResponse(Ticket ticket);
}
