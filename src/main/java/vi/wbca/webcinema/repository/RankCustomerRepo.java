package vi.wbca.webcinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.RankCustomer;

import java.util.Optional;

@Repository
public interface RankCustomerRepo extends JpaRepository<RankCustomer, Long> {
    Optional<RankCustomer> findByName(String name);
}
