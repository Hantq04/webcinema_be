package vi.wbca.webcinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.dto.cinema.FoodRevenueDTO;
import vi.wbca.webcinema.model.Bill;
import vi.wbca.webcinema.model.BillFood;
import vi.wbca.webcinema.model.Food;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BillFoodRepo extends JpaRepository<BillFood, Long> {
    @Query("SELECT bf FROM BillFood bf WHERE bf.bill.id = :billId")
    List<BillFood> findAllByBillId(Long billId);

    List<BillFood> findAllByBill(Bill bill);

    Optional<BillFood> findByBillAndFood(Bill bill, Food food);

    @Query("""
    SELECT new vi.wbca.webcinema.dto.cinema.FoodRevenueDTO(
        f.nameOfFood, SUM(bf.quantity)
    )
    FROM BillFood bf
    JOIN bf.food f
    WHERE bf.bill.createTime BETWEEN :start AND :end
    GROUP BY f.nameOfFood
    ORDER BY SUM(bf.quantity) DESC
    """)
    List<FoodRevenueDTO> getFoodRevenueSevenDays(LocalDateTime start, LocalDateTime end);
}
