package vi.wbca.webcinema.service.billTicketService;

import vi.wbca.webcinema.dto.BillTicketDTO;
import vi.wbca.webcinema.model.Bill;

public interface BillTicketService {
    void insertBillTicket(BillTicketDTO billTicketDTO, Bill bill);

    void deleteBillTicket(Long id);
}
