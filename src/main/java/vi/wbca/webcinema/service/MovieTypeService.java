package vi.wbca.webcinema.service;

import vi.wbca.webcinema.model.movie.MovieType;

import java.util.List;

public interface MovieTypeService {
    MovieType insertMovieType(MovieType movieType);

    List<MovieType> getAllType();
}
