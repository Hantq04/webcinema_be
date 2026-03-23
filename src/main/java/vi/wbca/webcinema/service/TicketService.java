package vi.wbca.webcinema.service;

import vi.wbca.webcinema.model.dto.ticket.TicketDTO;

public interface TicketService {
    void insertTicket(TicketDTO ticketDTO);

    void deleteTicket(String code);
}
