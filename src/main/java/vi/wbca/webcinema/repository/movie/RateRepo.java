package vi.wbca.webcinema.repository.movie;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.movie.Rate;

import java.util.Optional;

@Repository
public interface RateRepo extends JpaRepository<Rate, Long> {
    Optional<Rate> findByCode(String code);
}
