package vi.wbca.webcinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.Ticket;

@Repository
public interface TicketRepo extends JpaRepository<Ticket, Long> {
}
