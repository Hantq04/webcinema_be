package vi.wbca.webcinema.repository.cinema;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.cinema.Cinema;
import vi.wbca.webcinema.model.cinema.Room;

import java.util.Optional;

@Repository
public interface RoomRepo extends JpaRepository<Room, Long> {
    Optional<Room> findByNameAndCode(String name, String code);

    Optional<Room> findByCode(String code);

    Optional<Room> findByCodeAndCinema(String code, Cinema cinema);
}
