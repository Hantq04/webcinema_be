package vi.wbca.webcinema.repository.movie;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vi.wbca.webcinema.model.entity.movie.MovieType;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieTypeRepo extends JpaRepository<MovieType, Long> {
    Optional<MovieType> findByMovieTypeNameVi(String name);

    List<MovieType> findByIdInAndIsActiveTrue(List<Long> ids);
}
