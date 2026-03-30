package vi.wbca.webcinema.service;

import vi.wbca.webcinema.model.entity.bill.Bill;

import java.util.List;

public interface BillTicketService {
    void insertBillTicket(List<String> codes, Bill bill);

    void updateBillTicket(List<String> codes, Bill bill);

    void deleteTicket(Long id);

    void deleteBillTicket(Bill bill);
}
