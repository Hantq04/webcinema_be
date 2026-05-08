package vi.wbca.webcinema.repository.movie;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.entity.cinema.Room;
import vi.wbca.webcinema.model.entity.movie.Schedule;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepo extends JpaRepository<Schedule, Long> {
    Optional<Schedule> findByName(String name);

    Optional<Schedule> findByCodeAndMovieId(String code, Long movieId);

    Optional<Schedule> findByCode(String code);

    Optional<Schedule> findByStartAtAndRoom(LocalDateTime dateTime, Room room);

    List<Schedule> findAllByEndAtBeforeAndIsActiveTrue(LocalDateTime now);

    boolean existsByRoomAndStartAtBeforeAndEndAtAfter(Room room, LocalDateTime now1, LocalDateTime now2);

    @Query("SELECT COUNT(s) FROM Schedule s WHERE s.room = :room " +
    "AND (:startAt BETWEEN s.startAt AND s.endAt OR :endAt BETWEEN s.startAt AND s.endAt)")
    long countByRoomAndTimeOverlap(@Param("room") Room room,
                                       @Param("startAt") LocalDateTime startAt,
                                       @Param("endAt") LocalDateTime endAt);

    @Query("SELECT MAX(s.endAt) FROM Schedule s WHERE s.room.id = :roomId AND s.endAt <= :newStartAt")
    LocalDateTime findLastEndAt(@Param("roomId") Long roomId, @Param("newStartAt") LocalDateTime newStartAt);

    @Query("""
    SELECT DISTINCT s
    FROM Schedule s
    JOIN FETCH s.room r
    WHERE s.isActive = true
        AND s.startAt <= :endOfDay
        AND s.endAt >= :startOfDay
    """)
    List<Schedule> findActiveSchedulesOverlapping(@Param("startOfDay") LocalDateTime startOfDay,
                                                  @Param("endOfDay") LocalDateTime endOfDay);

    @Query("""
    SELECT DISTINCT s
    FROM Schedule s
    JOIN FETCH s.room r
    WHERE s.startAt <= :endOfDay
        AND s.endAt >= :startOfDay
    """)
    List<Schedule> findSchedulesOverlapping(@Param("startOfDay") LocalDateTime startOfDay,
                                            @Param("endOfDay") LocalDateTime endOfDay);

    @Query("""
    SELECT DISTINCT s
    FROM Schedule s
    JOIN FETCH s.room r
    JOIN FETCH r.cinema c
    WHERE s.movie.id = :movieId
    """)
    List<Schedule> findByMovieId(@Param("movieId") Long movieId);
}
