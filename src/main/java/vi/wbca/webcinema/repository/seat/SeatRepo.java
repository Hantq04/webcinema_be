package vi.wbca.webcinema.repository.seat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.dto.room.SeatByScheduleDTO;
import vi.wbca.webcinema.model.entity.bill.Bill;
import vi.wbca.webcinema.model.entity.cinema.Room;
import vi.wbca.webcinema.model.entity.seat.Seat;
import vi.wbca.webcinema.model.entity.seat.SeatStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepo extends JpaRepository<Seat, Long> {
    Optional<Seat> findByLineAndNumberAndRoom(String line, Integer number, Room room);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Seat s SET s.seatStatus = :status WHERE s.id IN (" +
            "SELECT t.seat.id FROM Ticket t WHERE t.id IN (" +
            "SELECT bt.ticket.id FROM BillTicket bt WHERE bt.bill = :bill))")
    void updateSeatStatusByBill(@Param("bill") Bill bill, @Param("status") SeatStatus status);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Seat s SET s.seatStatus = :status WHERE s.id IN (" +
        "SELECT t.seat.id FROM Ticket t WHERE t.code IN :codes)")
    void updateSeatStatusByTicketCodes(@Param("codes") List<String> codes, @Param("status") SeatStatus status);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Seat s SET s.seatStatus = :status WHERE s.id IN :seatIds")
    void updateSeatStatusByIds(@Param("seatIds") List<Long> seatIds, @Param("status") SeatStatus status);

    boolean existsByRoom(Room room);

    List<Seat> findByRoom(Room room);

    List<Seat> findAllByRoomOrderByLineAscNumberAsc(Room room);

    @Query("""
    SELECT new vi.wbca.webcinema.model.dto.room.SeatByScheduleDTO(
        s.id, s.line, s.number, s.pairIndex,
        CASE WHEN t.id IS NOT NULL THEN 'OCCUPIED' ELSE 'AVAILABLE' END,
        s.seatType.nameType
    )
    FROM Seat s
    JOIN s.room r
    JOIN r.schedules sch
    LEFT JOIN Ticket t ON t.schedule.id = sch.id AND t.seat = s AND t.isActive = true
    WHERE sch.code = :scheduleCode
    ORDER BY s.line, s.number
    """)
    List<SeatByScheduleDTO> getSeatsWithStatusBySchedule(@Param("scheduleCode") String scheduleCode);

    @Query("""
    SELECT COUNT(DISTINCT t.id)
    FROM Ticket t
    WHERE t.schedule.code = :scheduleCode AND t.isActive = true
    """)
    Integer countBookedSeatsBySchedule(@Param("scheduleCode") String scheduleCode);
}
