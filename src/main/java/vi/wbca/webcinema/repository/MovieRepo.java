package vi.wbca.webcinema.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.dto.movie.MovieResponseDTO;
import vi.wbca.webcinema.dto.movie.MovieStatisticDTO;
import vi.wbca.webcinema.model.Movie;

import java.util.Optional;

@Repository
public interface MovieRepo extends JpaRepository<Movie, Long> {
    Optional<Movie> findByName(String name);

    @Query("""
    SELECT new vi.wbca.webcinema.dto.movie.MovieStatisticDTO(
        m.id, m.name, m.movieType.movieTypeName, m.movieDuration, m.premiereDate, SUM(bt.quantity)
    )
    FROM Movie m
    JOIN m.schedules s
    JOIN s.tickets t
    JOIN t.billTickets bt
    WHERE t.isActive = false
    GROUP BY m.id, m.name, m.movieType.movieTypeName, m.movieDuration, m.premiereDate
    ORDER BY SUM(bt.quantity) DESC
    """)
    Page<MovieStatisticDTO> getTicketStatistics(Pageable pageable);

    @Query("""
    SELECT new vi.wbca.webcinema.dto.movie.MovieResponseDTO(
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
    SELECT new vi.wbca.webcinema.dto.movie.MovieResponseDTO(
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
    SELECT new vi.wbca.webcinema.dto.movie.MovieResponseDTO(
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
}
