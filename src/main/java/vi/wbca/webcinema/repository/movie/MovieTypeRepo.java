package vi.wbca.webcinema.repository.movie;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.movie.MovieType;

import java.util.Optional;

@Repository
public interface MovieTypeRepo extends JpaRepository<MovieType, Long> {
    Optional<MovieType> findByMovieTypeName(String name);
}
