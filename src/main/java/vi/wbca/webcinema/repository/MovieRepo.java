package vi.wbca.webcinema.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
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
}
