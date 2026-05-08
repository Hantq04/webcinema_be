package vi.wbca.webcinema.repository.cinema;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.entity.cinema.Cinema;
import vi.wbca.webcinema.model.entity.cinema.Room;

import java.util.Optional;
import java.util.List;

@Repository
public interface RoomRepo extends JpaRepository<Room, Long> {
    Optional<Room> findByNameAndCode(String name, String code);

    Optional<Room> findByCode(String code);

    Optional<Room> findByCodeAndCinema(String code, Cinema cinema);

    List<Room> findAllByCinemaOrderByCodeAsc(Cinema cinema);

    List<Room> findAllByCinemaAndIsActiveTrueOrderByCodeAsc(Cinema cinema);
}
