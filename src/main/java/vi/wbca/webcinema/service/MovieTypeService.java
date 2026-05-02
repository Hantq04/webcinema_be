package vi.wbca.webcinema.service;

import vi.wbca.webcinema.model.entity.movie.MovieType;
import vi.wbca.webcinema.model.response.MovieTypeResponse;

import java.util.List;

public interface MovieTypeService {
    MovieType insertMovieType(MovieType movieType);

    List<MovieTypeResponse> getAllType();
}
