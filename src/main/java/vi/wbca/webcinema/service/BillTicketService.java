package vi.wbca.webcinema.service;

import vi.wbca.webcinema.model.dto.bill.BillTicketDTO;
import vi.wbca.webcinema.model.entity.bill.Bill;

import java.util.List;

public interface BillTicketService {
    void insertBillTicket(BillTicketDTO billTicketDTO, Bill bill);

    void updateBillTicket(List<BillTicketDTO> billTicketDTO, Bill bill);

    void deleteTicket(Long id);

    void deleteBillTicket(Bill bill);
}
