package vi.wbca.webcinema.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import vi.wbca.webcinema.model.entity.movie.MovieType;
import vi.wbca.webcinema.model.response.MovieTypeResponse;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MovieTypeMapper {
    MovieTypeResponse toMovieTypeResponse(MovieType movieType);
}