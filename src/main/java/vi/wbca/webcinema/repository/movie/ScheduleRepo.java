package vi.wbca.webcinema.repository.movie;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.cinema.Room;
import vi.wbca.webcinema.model.movie.Schedule;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepo extends JpaRepository<Schedule, Long> {
    Optional<Schedule> findByName(String name);

    Optional<Schedule> findByCodeAndMovieId(String code, Long movieId);

    Optional<Schedule> findByCode(String code);

    Optional<Schedule> findByStartAtAndRoom(Date date, Room room);

    List<Schedule> findAllByEndAtBeforeAndIsActiveTrue(Date now);

    boolean existsByRoomAndStartAtBeforeAndEndAtAfter(Room room, Date now1, Date now2);

    @Query("SELECT COUNT(s) FROM Schedule s WHERE s.room = :room " +
    "AND (:startAt BETWEEN s.startAt AND s.endAt OR :endAt BETWEEN s.startAt AND s.endAt)")
    long countByRoomAndTimeOverlap(@Param("room") Room room,
                                       @Param("startAt") Date startAt,
                                       @Param("endAt") Date endAt);

    @Query("SELECT MAX(s.endAt) FROM Schedule s WHERE s.room.id = :roomId AND s.endAt <= :newStartAt")
    Date findLastEndAt(@Param("roomId") Long roomId, @Param("newStartAt") Date newStartAt);
}
