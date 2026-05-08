package vi.wbca.webcinema.repository.movie;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.entity.movie.Schedule;
import vi.wbca.webcinema.model.entity.seat.Seat;
import vi.wbca.webcinema.model.entity.movie.Ticket;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TicketRepo extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByCode(String code);

    Integer countBySchedule(Schedule schedule);

    Ticket findBySchedule(Schedule schedule);

    boolean existsByScheduleAndSeat(Schedule schedule, Seat seat);

        boolean existsByScheduleAndSeatAndIsActiveTrue(Schedule schedule, Seat seat);

    @Query("""
    SELECT COUNT(t)
    FROM Ticket t
    JOIN t.schedule s
    JOIN s.room r
    WHERE r.code = :roomCode
        AND t.isActive = true
    """)
    Long countActiveTicketsByRoomCode(@Param("roomCode") String roomCode);

    List<Ticket> findAllByCodeIn(Set<String> toAdd);

        @Query("""
        SELECT t
        FROM Ticket t
        WHERE t.isActive = true
            AND t.createTime < :threshold
            AND NOT EXISTS (
                    SELECT bt.id
                    FROM BillTicket bt
                    WHERE bt.ticket = t
            )
        """)
        List<Ticket> findExpiredTicketHolds(@Param("threshold") LocalDateTime threshold);
}
