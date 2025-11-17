package vi.wbca.webcinema.repository.bill;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.bill.BillStatus;

import java.util.Optional;

@Repository
public interface BillStatusRepo extends JpaRepository<BillStatus, Long> {
    Optional<BillStatus> findByName(String eBillStatus);
}
