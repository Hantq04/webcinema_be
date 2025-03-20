package vi.wbca.webcinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.dto.BillDTO;
import vi.wbca.webcinema.model.Bill;
import vi.wbca.webcinema.model.BillStatus;
import vi.wbca.webcinema.model.User;

import java.util.Optional;

@Repository
public interface BillRepo extends JpaRepository<Bill, Long> {
    Optional<Bill> findByTradingCode(String code);

    Optional<Bill> findByUser(User user);

    boolean existsByUserAndBillStatus(User user, BillStatus billStatus);
}
