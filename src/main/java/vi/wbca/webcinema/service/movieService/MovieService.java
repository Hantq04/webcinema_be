package vi.wbca.webcinema.service.movieService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vi.wbca.webcinema.dto.movie.MovieDTO;
import vi.wbca.webcinema.dto.movie.MovieStatisticDTO;

public interface MovieService {
    MovieDTO insertMovie(MovieDTO movieDTO);

    void updateMovie(MovieDTO movieDTO);

    void deleteMovie(String name);

    Page<MovieDTO> getMoviePage(Pageable pageable);

    Page<MovieStatisticDTO> sortMovieByTicketOrder(Pageable pageable);
}
