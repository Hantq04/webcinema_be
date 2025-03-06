package vi.wbca.webcinema.service.movieTypeService;

import vi.wbca.webcinema.model.MovieType;

import java.util.List;

public interface MovieTypeService {
    MovieType insertMovieType(MovieType movieType);

    List<MovieType> getAllType();
}
