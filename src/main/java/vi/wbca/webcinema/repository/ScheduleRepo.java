package vi.wbca.webcinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.Room;
import vi.wbca.webcinema.model.Schedule;

import java.util.Date;
import java.util.Optional;

@Repository
public interface ScheduleRepo extends JpaRepository<Schedule, Long> {
    Optional<Schedule> findByName(String name);

    Optional<Schedule> findByCodeAndMovieId(String code, Long movieId);

    Optional<Schedule> findByCode(String code);

    boolean existsByRoomAndStartAt(Room room, Date startAt);
}
