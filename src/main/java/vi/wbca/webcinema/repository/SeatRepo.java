package vi.wbca.webcinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.Seat;

@Repository
public interface SeatRepo extends JpaRepository<Seat, Long> {
}
