package vi.wbca.webcinema.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.dto.MovieDTO;
import vi.wbca.webcinema.model.Movie;

import java.util.Optional;

@Repository
public interface MovieRepo extends JpaRepository<Movie, Long> {
    Optional<Movie> findByName(String name);

    Page<MovieDTO> getTicketStatistics(Pageable pageable);
}
