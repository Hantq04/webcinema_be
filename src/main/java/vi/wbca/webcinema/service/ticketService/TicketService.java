package vi.wbca.webcinema.service.ticketService;

import vi.wbca.webcinema.dto.ticket.TicketDTO;

public interface TicketService {
    void insertTicket(TicketDTO ticketDTO);

    void deleteTicket(String code);
}
