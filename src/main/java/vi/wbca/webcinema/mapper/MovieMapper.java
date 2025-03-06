package vi.wbca.webcinema.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vi.wbca.webcinema.dto.MovieDTO;
import vi.wbca.webcinema.model.Movie;

@Mapper(componentModel = "spring")
public interface MovieMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "schedules", ignore = true)
    Movie toMovie(MovieDTO movieDTO);

    @Mapping(target = "movieTypeName", source = "movie.movieType.movieTypeName")
    @Mapping(target = "code", source = "movie.rate.code")
    MovieDTO toMovieDTO(Movie movie);
}
