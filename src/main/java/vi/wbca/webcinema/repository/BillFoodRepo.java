package vi.wbca.webcinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.Bill;
import vi.wbca.webcinema.model.BillFood;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillFoodRepo extends JpaRepository<BillFood, Long> {
    @Query("SELECT bf FROM BillFood bf WHERE bf.bill.id = :billId")
    List<BillFood> findAllByBillId(Long billId);

    List<BillFood> findAllByBill(Bill bill);
}
