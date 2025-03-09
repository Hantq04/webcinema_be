package vi.wbca.webcinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.enums.EBillStatus;
import vi.wbca.webcinema.model.BillStatus;

import java.util.Optional;

@Repository
public interface BillStatusRepo extends JpaRepository<BillStatus, Long> {
    Optional<BillStatus> findByName(String eBillStatus);
}
