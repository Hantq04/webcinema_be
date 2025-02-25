package vi.wbca.webcinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.BillStatus;

@Repository
public interface BillStatusRepo extends JpaRepository<BillStatus, Long> {
}
