package vi.wbca.webcinema.repository.bill;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.entity.bill.Bill;
import vi.wbca.webcinema.model.entity.bill.BillTicket;
import vi.wbca.webcinema.model.entity.movie.Ticket;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface BillTicketRepo extends JpaRepository<BillTicket, Long> {
    @Query("SELECT bt FROM BillTicket bt WHERE bt.bill.id = :billId")
    List<BillTicket> findAllByBillId(Long billId);

    @Query("SELECT bt FROM BillTicket bt WHERE bt.bill.id IN :billIds")
    List<BillTicket> findAllByBillIdIn(List<Long> billIds);

    List<BillTicket> findAllByBill(Bill bill);

    @Modifying
    @Query("DELETE FROM BillTicket bt WHERE bt.bill.id = :billId")
    void deleteAllByBillId(Long billId);

    @Query("SELECT DISTINCT bt.bill FROM BillTicket bt WHERE bt.ticket.code IN :codes")
    List<Bill> findDistinctBillsByTicketCodes(Set<String> codes);

    Optional<BillTicket> findByBillAndTicket(Bill bill, Ticket ticket);
}
