package vi.wbca.webcinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.Bill;

@Repository
public interface BillRepo extends JpaRepository<Bill, Long> {
}
