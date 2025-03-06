package vi.wbca.webcinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.Rate;

import java.util.Optional;

@Repository
public interface RateRepo extends JpaRepository<Rate, Long> {
    Optional<Rate> findByCode(String code);
}
