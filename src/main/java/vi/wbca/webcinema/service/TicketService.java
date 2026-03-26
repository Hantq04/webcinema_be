package vi.wbca.webcinema.service;

import vi.wbca.webcinema.model.dto.ticket.TicketDTO;
import vi.wbca.webcinema.model.request.BookingRequest;

public interface TicketService {
    void insertTicket(BookingRequest request);

    void deleteTicket(String code);
}
