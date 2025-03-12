package vi.wbca.webcinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.enums.ESeatStatus;
import vi.wbca.webcinema.model.Room;
import vi.wbca.webcinema.model.Seat;
import vi.wbca.webcinema.model.SeatStatus;

import java.util.Optional;

@Repository
public interface SeatRepo extends JpaRepository<Seat, Long> {
    Optional<Seat> findByLineAndNumberAndRoom(String line, Integer number, Room room);

    Integer countByRoom(Room room);
}
