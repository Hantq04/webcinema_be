package vi.wbca.webcinema.repository.bill;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.dto.cinema.CinemaRevenueDTO;
import vi.wbca.webcinema.model.entity.bill.BillStatus;
import vi.wbca.webcinema.model.entity.bill.Bill;
import vi.wbca.webcinema.model.entity.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepo extends JpaRepository<Bill, Long> {
    Optional<Bill> findByTradingCode(String code);

    @Query("""
    SELECT DISTINCT b
    FROM Bill b
    LEFT JOIN FETCH b.user
    LEFT JOIN FETCH b.billStatus
    LEFT JOIN FETCH b.billTickets bt
    LEFT JOIN FETCH bt.ticket t
    LEFT JOIN FETCH t.seat seat
    LEFT JOIN FETCH t.schedule schedule
    LEFT JOIN FETCH schedule.movie movie
    LEFT JOIN FETCH schedule.room room
    LEFT JOIN FETCH room.cinema cinema
    WHERE b.tradingCode = :tradingCode
    """)
    Optional<Bill> findDetailByTradingCode(@Param("tradingCode") String tradingCode);

    Optional<Bill> findByUser(User user);

    Page<Bill> findAllByUser(User user, Pageable pageable);

    Page<Bill> findAllByUserAndBillStatus(User user, BillStatus billStatus, Pageable pageable);

    Optional<Bill> findByUserAndBillStatus(User user, BillStatus billStatus);

    List<Bill> findAllByUserAndBillStatusOrderByPaidAtDesc(User user, BillStatus billStatus);

        Page<Bill> findAllByIsActiveTrue(Pageable pageable);

        Page<Bill> findAllByUserAndIsActiveTrue(User user, Pageable pageable);

        @Query(value = """
        SELECT DISTINCT b
        FROM Bill b
        JOIN b.billTickets bt
        JOIN bt.ticket t
        JOIN t.schedule s
        JOIN s.room r
        JOIN r.cinema c
        WHERE b.isActive = true
            AND c.id = :cinemaId
        """,
        countQuery = """
        SELECT COUNT(DISTINCT b)
        FROM Bill b
        JOIN b.billTickets bt
        JOIN bt.ticket t
        JOIN t.schedule s
        JOIN s.room r
        JOIN r.cinema c
        WHERE b.isActive = true
            AND c.id = :cinemaId
        """)
        Page<Bill> findAllByCinemaIdAndIsActiveTrue(Long cinemaId, Pageable pageable);

    @Query(value = """
        SELECT DISTINCT b
        FROM Bill b
        JOIN b.billTickets bt
        JOIN bt.ticket t
        JOIN t.schedule s
        JOIN s.room r
        JOIN r.cinema c
        WHERE c.id = :cinemaId
        """,
        countQuery = """
        SELECT COUNT(DISTINCT b)
        FROM Bill b
        JOIN b.billTickets bt
        JOIN bt.ticket t
        JOIN t.schedule s
        JOIN s.room r
        JOIN r.cinema c
        WHERE c.id = :cinemaId
        """)
    Page<Bill> findAllByCinemaId(Long cinemaId, Pageable pageable);

    boolean existsByUserAndBillStatus(User user, BillStatus billStatus);

    List<Bill> findAllByBillStatusAndCreateTimeBeforeAndIsActiveTrue(BillStatus billStatus, LocalDateTime createTime);

    @Query("""
    SELECT DISTINCT b
    FROM Bill b
    JOIN FETCH b.billStatus
    JOIN b.billTickets bt
    JOIN bt.ticket t
    JOIN t.schedule s
    JOIN s.room r
    WHERE r.code = :roomCode
    """)
    List<Bill> findDistinctBillsWithStatusByRoomCode(@Param("roomCode") String roomCode);

    @Query("""
    SELECT b
    FROM Bill b
    JOIN b.billTickets bt
    JOIN bt.ticket t
    JOIN t.schedule s
    WHERE b.isActive = true
        AND b.billStatus = :successStatus
    GROUP BY b
    HAVING MAX(s.endAt) < :currentTime
    """)
    List<Bill> findAllActiveSuccessBillsWithEndedShowtime(
            @Param("successStatus") BillStatus successStatus,
            @Param("currentTime") LocalDateTime currentTime
    );

    List<Bill> findAllByCreateTimeBeforeAndIsActiveTrue(LocalDateTime createTime);

    @Query("""
    SELECT new vi.wbca.webcinema.model.dto.cinema.CinemaRevenueDTO(
        c.nameOfCinema, c.code, SUM(b.totalMoney)
    )
    FROM Bill b
    JOIN b.billTickets bt
    JOIN bt.ticket t
    JOIN t.schedule s
    JOIN s.room r
    JOIN r.cinema c
    WHERE b.paidAt BETWEEN :start AND :end
        AND b.billStatus.name = :successStatus
    GROUP BY c.nameOfCinema, c.code
    ORDER BY SUM(b.totalMoney) DESC
    """)
    List<CinemaRevenueDTO> getRevenueWithTime(@Param("start") LocalDateTime start,
                                              @Param("end") LocalDateTime end,
                                              @Param("successStatus") String successStatus);

    @Query("""
    SELECT b
    FROM Bill b
    WHERE b.paidAt BETWEEN :start AND :end
        AND b.billStatus.name = :successStatus
    """)
    List<Bill> findAllSuccessfulByCreateTimeBetween(@Param("start") LocalDateTime start,
                                                    @Param("end") LocalDateTime end,
                                                    @Param("successStatus") String successStatus);

    @Query("""
    SELECT b.id
    FROM Bill b
    WHERE b.billStatus.name = :successStatus
        AND b.paidAt IS NOT NULL
        AND b.paidAt BETWEEN :start AND :end
    ORDER BY b.paidAt DESC
    """)
    List<Long> findRecentSuccessfulBillIds(@Param("successStatus") String successStatus,
                                           @Param("start") LocalDateTime start,
                                           @Param("end") LocalDateTime end);

    @Query("""
    SELECT DISTINCT b
    FROM Bill b
    LEFT JOIN FETCH b.user
    LEFT JOIN FETCH b.billStatus
    LEFT JOIN FETCH b.billTickets bt
    LEFT JOIN FETCH bt.ticket t
    LEFT JOIN FETCH t.seat
    LEFT JOIN FETCH t.schedule s
    LEFT JOIN FETCH s.movie
    LEFT JOIN FETCH s.room
    WHERE b.id IN :billIds
    """)
    List<Bill> findBillsWithOverviewDataByIds(@Param("billIds") List<Long> billIds);

    @Query("""
    SELECT DISTINCT b
    FROM Bill b
    JOIN b.billTickets bt
    JOIN bt.ticket t
    JOIN t.schedule s
    JOIN s.room r
    JOIN r.cinema c
    JOIN s.movie m
    WHERE b.paidAt BETWEEN :start AND :end
        AND b.billStatus.name = :successStatus
        AND (:cinemaId IS NULL OR c.id = :cinemaId)
        AND (:roomId IS NULL OR r.id = :roomId)
        AND (:movieId IS NULL OR m.id = :movieId)
    """)
    List<Bill> findRevenueBills(@Param("start") LocalDateTime start,
                                @Param("end") LocalDateTime end,
                                @Param("successStatus") String successStatus,
                                @Param("cinemaId") Long cinemaId,
                                @Param("roomId") Long roomId,
                                @Param("movieId") Long movieId);
}
