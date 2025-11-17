package vi.wbca.webcinema.repository.seat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.seat.SeatStatus;

import java.util.Optional;

@Repository
public interface SeatStatusRepo extends JpaRepository<SeatStatus, Long> {
    Optional<SeatStatus> findByCode(String name);
}
