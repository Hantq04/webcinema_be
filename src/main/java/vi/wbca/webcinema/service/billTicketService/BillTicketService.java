package vi.wbca.webcinema.service.billTicketService;

import vi.wbca.webcinema.dto.BillTicketDTO;

public interface BillTicketService {
    void insertBillTicket(BillTicketDTO billTicketDTO);

    void deleteBillTicket(Long id);
}
