package vi.wbca.webcinema.service.movieService;

import vi.wbca.webcinema.dto.MovieDTO;

public interface MovieService {
    MovieDTO insertMovie(MovieDTO movieDTO);
    void updateMovie(MovieDTO movieDTO);
    void deleteMovie(String name);
}
