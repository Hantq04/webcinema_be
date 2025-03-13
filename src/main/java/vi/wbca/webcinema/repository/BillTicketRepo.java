package vi.wbca.webcinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.Bill;
import vi.wbca.webcinema.model.BillTicket;
import vi.wbca.webcinema.model.Ticket;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillTicketRepo extends JpaRepository<BillTicket, Long> {
    @Query("SELECT bt FROM BillTicket bt WHERE bt.bill.id = :billId")
    List<BillTicket> findAllByBillId(Long billId);

    List<BillTicket> findAllByBill(Bill bill);

    Optional<BillTicket> findByBillAndTicket(Bill bill, Ticket ticket);
}
