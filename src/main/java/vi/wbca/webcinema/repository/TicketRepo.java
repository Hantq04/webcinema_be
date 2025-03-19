package vi.wbca.webcinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.BillTicket;
import vi.wbca.webcinema.model.Schedule;
import vi.wbca.webcinema.model.Seat;
import vi.wbca.webcinema.model.Ticket;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepo extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByCode(String code);

    Integer countBySchedule(Schedule schedule);
}
