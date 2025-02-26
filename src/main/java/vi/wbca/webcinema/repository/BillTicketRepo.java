package vi.wbca.webcinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.BillTicket;

@Repository
public interface BillTicketRepo extends JpaRepository<BillTicket, Long> {
}
