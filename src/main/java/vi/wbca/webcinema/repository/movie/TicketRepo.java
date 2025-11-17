package vi.wbca.webcinema.repository.movie;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.movie.Schedule;
import vi.wbca.webcinema.model.seat.Seat;
import vi.wbca.webcinema.model.movie.Ticket;

import java.util.Optional;

@Repository
public interface TicketRepo extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByCode(String code);

    Integer countBySchedule(Schedule schedule);

    Ticket findBySchedule(Schedule schedule);

    boolean existsByScheduleAndSeat(Schedule schedule, Seat seat);
}
