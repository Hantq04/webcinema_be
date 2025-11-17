package vi.wbca.webcinema.repository.bill;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.bill.Bill;
import vi.wbca.webcinema.model.bill.BillTicket;
import vi.wbca.webcinema.model.movie.Ticket;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillTicketRepo extends JpaRepository<BillTicket, Long> {
    @Query("SELECT bt FROM BillTicket bt WHERE bt.bill.id = :billId")
    List<BillTicket> findAllByBillId(Long billId);

    List<BillTicket> findAllByBill(Bill bill);

    Optional<BillTicket> findByBillAndTicket(Bill bill, Ticket ticket);
}
