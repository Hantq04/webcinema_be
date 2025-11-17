package vi.wbca.webcinema.repository.bill;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.dto.cinema.CinemaRevenueDTO;
import vi.wbca.webcinema.model.bill.Bill;
import vi.wbca.webcinema.model.bill.BillStatus;
import vi.wbca.webcinema.model.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepo extends JpaRepository<Bill, Long> {
    Optional<Bill> findByTradingCode(String code);

    Optional<Bill> findByUser(User user);

    boolean existsByUserAndBillStatus(User user, BillStatus billStatus);

    @Query("""
    SELECT new vi.wbca.webcinema.dto.cinema.CinemaRevenueDTO(
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
