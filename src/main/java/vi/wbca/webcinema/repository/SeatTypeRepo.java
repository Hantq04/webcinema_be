package vi.wbca.webcinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.SeatType;

@Repository
public interface SeatTypeRepo extends JpaRepository<SeatType, Long> {
}
