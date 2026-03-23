package vi.wbca.webcinema.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vi.wbca.webcinema.model.dto.movie.MovieDTO;
import vi.wbca.webcinema.model.entity.movie.Movie;

@Mapper(componentModel = "spring")
public interface MovieMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "schedules", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "movieType", ignore = true)
    @Mapping(target = "rate", ignore = true)
    Movie toMovie(MovieDTO movieDTO);

    @Mapping(target = "movieTypeName", source = "movieType.movieTypeName")
    @Mapping(target = "code", source = "rate.code")
    MovieDTO toMovieDTO(Movie movie);
}
