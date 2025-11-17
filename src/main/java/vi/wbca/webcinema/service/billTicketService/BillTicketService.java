package vi.wbca.webcinema.service.billTicketService;

import vi.wbca.webcinema.dto.bill.BillTicketDTO;
import vi.wbca.webcinema.model.bill.Bill;

import java.util.List;

public interface BillTicketService {
    void insertBillTicket(BillTicketDTO billTicketDTO, Bill bill);

    void updateBillTicket(List<BillTicketDTO> billTicketDTO, Bill bill);

    void deleteTicket(Long id);

    void deleteBillTicket(Bill bill);
}
