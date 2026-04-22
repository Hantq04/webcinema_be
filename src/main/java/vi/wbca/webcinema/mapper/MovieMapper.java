package vi.wbca.webcinema.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vi.wbca.webcinema.model.dto.movie.MovieDTO;
import vi.wbca.webcinema.model.dto.movie.MovieNowShowingDTO;
import vi.wbca.webcinema.model.entity.movie.Movie;
import vi.wbca.webcinema.model.response.MovieShowingResponse;

@Mapper(componentModel = "spring")
public interface MovieMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "schedules", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "banner", ignore = true)
    @Mapping(target = "movieType", ignore = true)
    @Mapping(target = "rate", ignore = true)
    @Mapping(target = "endDate", ignore = true)
    @Mapping(target = "image", ignore = true)
    Movie toMovie(MovieDTO movieDTO);

    @Mapping(target = "movieType", source = "movieType.movieTypeName")
    @Mapping(target = "code", source = "rate.code")
    @Mapping(target = "bannerId", source = "banner.id")
    MovieDTO toMovieDTO(Movie movie);

    @Mapping(target = "image", source = "banner.imageUrl")
    @Mapping(target = "rate", source = "rate.code")
    MovieNowShowingDTO toMovieNowShowingDTO(Movie movie);

    @Mapping(target = "image", source = "banner.imageUrl")
    @Mapping(target = "rate", source = "rate.code")
    @Mapping(target = "movieType", source = "movieType.movieTypeName")
    @Mapping(target = "duration", source = "movieDuration")
    MovieShowingResponse toMovieShowingResponse(Movie movie);
}
