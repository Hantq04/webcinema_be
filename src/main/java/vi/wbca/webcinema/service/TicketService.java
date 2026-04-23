package vi.wbca.webcinema.service;

import vi.wbca.webcinema.model.request.BookingRequest;
import vi.wbca.webcinema.model.response.BookingResponse;
import vi.wbca.webcinema.model.response.TicketResponse;

import java.util.List;

public interface TicketService {
    BookingResponse insertTicket(BookingRequest request);

    void deleteTicket(String code);

    List<TicketResponse> getAllTicket();
}
