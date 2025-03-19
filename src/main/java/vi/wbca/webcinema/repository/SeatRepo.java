package vi.wbca.webcinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.Bill;
import vi.wbca.webcinema.model.Room;
import vi.wbca.webcinema.model.Seat;
import vi.wbca.webcinema.model.SeatStatus;

import java.util.Optional;

@Repository
public interface SeatRepo extends JpaRepository<Seat, Long> {
    Optional<Seat> findByLineAndNumberAndRoom(String line, Integer number, Room room);

    Integer countByRoom(Room room);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Seat s SET s.seatStatus = :status WHERE s.id IN (" +
            "SELECT t.seat.id FROM Ticket t WHERE t.id IN (" +
            "SELECT bt.ticket.id FROM BillTicket bt WHERE bt.bill = :bill))")
    void updateSeatStatusByBill(@Param("bill") Bill bill, @Param("status") SeatStatus status);
}
