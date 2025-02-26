package vi.wbca.webcinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.SeatStatus;

@Repository
public interface SeatStatusRepo extends JpaRepository<SeatStatus, Long> {
}
