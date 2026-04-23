package vi.wbca.webcinema.repository.movie;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.entity.movie.Movie;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepo extends JpaRepository<Movie, Long> {
    Optional<Movie> findByNameAndIsActive(String name, boolean isActive);

    Optional<Movie> findByCodeAndIsActive(String code, boolean isActive);

    @Modifying
    @Transactional
    @Query("UPDATE Movie m SET m.isActive = false WHERE m.isActive = true AND m.endDate < :now")
    void updateExpiredMovies(@Param("now") LocalDateTime now);

    @Query("""
    SELECT DISTINCT m
    FROM Movie m
    JOIN m.schedules s
    JOIN s.tickets t
    JOIN t.billTickets bt
    WHERE t.isActive = false
    ORDER BY COUNT(bt.id) DESC
    """)
    Page<Movie> getTicketStatistics(Pageable pageable);

    @Query("""
    SELECT COUNT(DISTINCT bt.id)
    FROM Movie m
    JOIN m.schedules s
    JOIN s.tickets t
    JOIN t.billTickets bt
    WHERE m.id = :movieId AND t.isActive = false
    """)
    Long countBookedTicketsByMovieId(@Param("movieId") Long movieId);

    @Query(value = """
    SELECT DISTINCT m
    FROM Movie m
    JOIN m.schedules s
    JOIN s.room r
    JOIN r.cinema c
    WHERE c.id = :cinemaId
    """,
    countQuery = """
    SELECT COUNT(DISTINCT m)
    FROM Movie m
    JOIN m.schedules s
    JOIN s.room r
    JOIN r.cinema c
    WHERE c.id = :cinemaId
    """)
    Page<Movie> getMovieWithCinema(Long cinemaId, Pageable pageable);

    @Query(value = """
    SELECT DISTINCT m
    FROM Movie m
    JOIN m.schedules s
    JOIN s.room r
    WHERE r.id = :roomId
    """,
    countQuery = """
    SELECT COUNT(DISTINCT m)
    FROM Movie m
    JOIN m.schedules s
    JOIN s.room r
    WHERE r.id = :roomId
    """)
    Page<Movie> getMovieWithRoom(Long roomId, Pageable pageable);

    @Query(value = """
    SELECT DISTINCT m
    FROM Movie m
    JOIN m.schedules s
    JOIN s.room r
    JOIN r.seats st
    JOIN st.seatStatus ss
    WHERE ss.id = :seatStatusId
    """,
    countQuery = """
    SELECT COUNT(DISTINCT m)
    FROM Movie m
    JOIN m.schedules s
    JOIN s.room r
    JOIN r.seats st
    JOIN st.seatStatus ss
    WHERE ss.id = :seatStatusId
    """)
    Page<Movie> getMovieWithSeatStatus(Long seatStatusId, Pageable pageable);

    @Query("""
    SELECT DISTINCT m FROM Movie m
    LEFT JOIN m.movieTypes mt
    WHERE m.isActive = true
    AND (
        (:nowShowing = false OR
            (m.premiereDate <= :now AND (m.endDate IS NULL OR m.endDate >= :now))
        )
    )
    AND (
        (:comingSoon = false OR m.premiereDate > :now)
    )
    AND (
        (:genre IS NULL OR mt.movieTypeNameVi = :genre)
    )
    ORDER BY m.premiereDate DESC
    """)
    List<Movie> filterMovies(
            @Param("now") LocalDateTime now,
            @Param("nowShowing") boolean nowShowing,
            @Param("comingSoon") boolean comingSoon,
            @Param("genre") String genre
    );

    @Query("""
    SELECT m
    FROM Movie m
    WHERE m.isActive = true
    AND m.premiereDate <= :now
    AND (m.endDate IS NULL OR m.endDate >= :now)
    ORDER BY (
        SELECT COUNT(bt.id)
        FROM BillTicket bt
        JOIN bt.ticket t
        JOIN t.schedule s
        WHERE s.movie = m
    ) DESC, m.premiereDate DESC
    """)
    List<Movie> findHotNowShowingMovies(@Param("now") LocalDateTime now);
}
