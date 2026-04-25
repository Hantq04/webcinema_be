package vi.wbca.webcinema.repository.bill;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.dto.cinema.CinemaRevenueDTO;
import vi.wbca.webcinema.model.entity.bill.Bill;
import vi.wbca.webcinema.model.entity.bill.BillStatus;
import vi.wbca.webcinema.model.entity.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepo extends JpaRepository<Bill, Long> {
    Optional<Bill> findByTradingCode(String code);

    Optional<Bill> findByUser(User user);

    Optional<Bill> findByUserAndBillStatus(User user, BillStatus billStatus);

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

    boolean existsByUserAndBillStatus(User user, BillStatus billStatus);

    List<Bill> findAllByBillStatusAndCreateTimeBeforeAndIsActiveTrue(BillStatus billStatus, LocalDateTime createTime);

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
    WHERE b.createTime BETWEEN :start AND :end
    GROUP BY c.nameOfCinema, c.code
    ORDER BY SUM(b.totalMoney) DESC
    """)
    List<CinemaRevenueDTO> getRevenueWithTime(LocalDateTime start, LocalDateTime end);
}
