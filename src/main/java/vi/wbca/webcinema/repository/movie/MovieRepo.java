package vi.wbca.webcinema.repository.movie;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.dto.movie.MovieResponseDTO;
import vi.wbca.webcinema.model.dto.movie.MovieStatisticDTO;
import vi.wbca.webcinema.model.entity.movie.Movie;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepo extends JpaRepository<Movie, Long> {
    Optional<Movie> findByNameAndIsActive(String name, boolean isActive);

    @Modifying
    @Transactional
    @Query("UPDATE Movie m SET m.isActive = false WHERE m.isActive = true AND m.endDate < :now")
    void updateExpiredMovies(@Param("now") LocalDateTime now);

    @Query("""
    SELECT new vi.wbca.webcinema.model.dto.movie.MovieStatisticDTO(
        m.id, m.name, m.movieType.movieTypeName, m.movieDuration, m.premiereDate, COUNT(bt.id)
    )
    FROM Movie m
    JOIN m.schedules s
    JOIN s.tickets t
    JOIN t.billTickets bt
    WHERE t.isActive = false
    GROUP BY m.id, m.name, m.movieType.movieTypeName, m.movieDuration, m.premiereDate
    ORDER BY COUNT(bt.id) DESC
    """)
    Page<MovieStatisticDTO> getTicketStatistics(Pageable pageable);

    @Query("""
    SELECT new vi.wbca.webcinema.model.dto.movie.MovieResponseDTO(
        m.id, m.name, m.movieType.movieTypeName, m.movieDuration, m.premiereDate
    )
    FROM Movie m
    JOIN m.schedules s
    JOIN s.room r
    JOIN r.cinema c
    WHERE c.id = :cinemaId
    GROUP BY m.id, m.name, m.movieType.movieTypeName, m.movieDuration, m.premiereDate
    """)
    Page<MovieResponseDTO> getMovieWithCinema(Long cinemaId, Pageable pageable);

    @Query("""
    SELECT new vi.wbca.webcinema.model.dto.movie.MovieResponseDTO(
        m.id, m.name, m.movieType.movieTypeName, m.movieDuration, m.premiereDate
    )
    FROM Movie m
    JOIN m.schedules s
    JOIN s.room r
    WHERE r.id = :roomId
    GROUP BY m.id, m.name, m.movieType.movieTypeName, m.movieDuration, m.premiereDate
    """)
    Page<MovieResponseDTO> getMovieWithRoom(Long roomId, Pageable pageable);

    @Query("""
    SELECT new vi.wbca.webcinema.model.dto.movie.MovieResponseDTO(
        m.id, m.name, m.movieType.movieTypeName, m.movieDuration, m.premiereDate
    )
    FROM Movie m
    JOIN m.schedules s
    JOIN s.room r
    JOIN r.seats st
    JOIN st.seatStatus ss
    WHERE ss.id = :seatStatusId
    GROUP BY m.id, m.name, m.movieType.movieTypeName, m.movieDuration, m.premiereDate
    """)
    Page<MovieResponseDTO> getMovieWithSeatStatus(Long seatStatusId, Pageable pageable);

    @Query("""
    SELECT m FROM Movie m
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
        (:genre IS NULL OR m.movieType.movieTypeName = :genre)
    )
    ORDER BY m.premiereDate DESC
    """)
    List<Movie> filterMovies(
            @Param("now") LocalDateTime now,
            @Param("nowShowing") boolean nowShowing,
            @Param("comingSoon") boolean comingSoon,
            @Param("genre") String genre
    );
}
