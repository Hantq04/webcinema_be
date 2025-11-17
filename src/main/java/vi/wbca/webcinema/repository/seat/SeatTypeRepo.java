package vi.wbca.webcinema.repository.seat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.seat.SeatType;

import java.util.Optional;

@Repository
public interface SeatTypeRepo extends JpaRepository<SeatType, Long> {
    Optional<SeatType> findByNameType(String name);
}
