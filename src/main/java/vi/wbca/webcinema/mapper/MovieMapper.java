package vi.wbca.webcinema.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vi.wbca.webcinema.model.dto.movie.MovieDTO;
import vi.wbca.webcinema.model.dto.movie.MovieNowShowingDTO;
import vi.wbca.webcinema.model.dto.movie.MovieResponseDTO;
import vi.wbca.webcinema.model.dto.movie.MovieStatisticDTO;
import vi.wbca.webcinema.model.entity.movie.Movie;
import vi.wbca.webcinema.model.entity.movie.MovieType;
import vi.wbca.webcinema.model.response.MovieDetailResponse;
import vi.wbca.webcinema.model.response.MovieShowingResponse;
import vi.wbca.webcinema.util.Constants;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface MovieMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "movieTypes", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "banner", ignore = true)
    @Mapping(target = "rate", ignore = true)
    @Mapping(target = "endDate", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "nameEn", ignore = true)
    @Mapping(target = "descriptionEn", ignore = true)
    @Mapping(target = "schedules", ignore = true)
    Movie toMovie(MovieDTO movieDTO);

    @Mapping(target = "movieTypeIds", expression = "java(extractMovieTypeIds(movie.getMovieTypes()))")
    @Mapping(target = "rate", source = "rate.code")
    @Mapping(target = "bannerId", source = "banner.id")
    @Mapping(target = "image", source = "banner.imageUrl")
    @Mapping(target = "nameEn", source = "nameEn")
    @Mapping(target = "descriptionEn", source = "descriptionEn")
    MovieDTO toMovieDTO(Movie movie);

    @Mapping(target = "image", source = "banner.imageUrl")
    @Mapping(target = "code", source = "code")
    @Mapping(target = "rate", source = "rate.code")
    @Mapping(target = "nameEn", source = "nameEn")
    MovieNowShowingDTO toMovieNowShowingDTO(Movie movie);

    @Mapping(target = "movieTypeName", expression = "java(joinMovieTypeNamesVi(movie.getMovieTypes()))")
    @Mapping(target = "movieTypeNameEn", expression = "java(joinMovieTypeNamesEn(movie.getMovieTypes()))")
    MovieResponseDTO toMovieResponseDTO(Movie movie);

    @Mapping(target = "movieTypeName", expression = "java(joinMovieTypeNamesVi(movie.getMovieTypes()))")
    @Mapping(target = "movieTypeNameEn", expression = "java(joinMovieTypeNamesEn(movie.getMovieTypes()))")
    @Mapping(target = "totalTicketsBooked", ignore = true)
    MovieStatisticDTO toMovieStatisticDTO(Movie movie);

    @Mapping(target = "image", source = "banner.imageUrl")
    @Mapping(target = "rate", source = "rate.code")
    @Mapping(target = "rateEn", source = "rate.descriptionEn")
    @Mapping(target = "movieType", expression = "java(extractMovieTypeNamesVi(movie.getMovieTypes()))")
    @Mapping(target = "movieTypeEn", expression = "java(extractMovieTypeNamesEn(movie.getMovieTypes()))")
    @Mapping(target = "code", source = "code")
    @Mapping(target = "duration", source = "movieDuration")
    MovieShowingResponse toMovieShowingResponse(Movie movie);

    @Mapping(target = "code", source = "code")
    @Mapping(target = "image", source = "image")
    @Mapping(target = "bannerId", source = "banner.id")
    @Mapping(target = "nameEn", source = "nameEn")
    @Mapping(target = "movieType", expression = "java(extractMovieTypeNamesVi(movie.getMovieTypes()))")
    @Mapping(target = "movieTypeEn", expression = "java(extractMovieTypeNamesEn(movie.getMovieTypes()))")
    @Mapping(target = "premiereDate", expression = "java(formatDate(movie.getPremiereDate()))")
    @Mapping(target = "endDate", expression = "java(formatDate(movie.getEndDate()))")
    @Mapping(target = "duration", source = "movieDuration")
    @Mapping(target = "rate", source = "rate.code")
    @Mapping(target = "rateEn", source = "rate.descriptionEn")
    @Mapping(target = "rateName", source = "rate.descriptionVi")
    @Mapping(target = "rateNameEn", source = "rate.descriptionEn")
    @Mapping(target = "trailerUrl", source = "trailer")
    @Mapping(target = "descriptionEn", source = "descriptionEn")
    MovieDetailResponse toMovieDetailResponse(Movie movie);

    default String formatDate(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.format(Constants.DATE_TIME_FORMATTER);
    }

    default List<Long> extractMovieTypeIds(List<MovieType> movieTypes) {
        return movieTypes == null ? List.of() : movieTypes.stream().map(MovieType::getId).toList();
    }

    default List<String> extractMovieTypeNamesVi(List<MovieType> movieTypes) {
        return movieTypes == null ? List.of() : movieTypes.stream()
                .map(MovieType::getMovieTypeNameVi)
                .filter(name -> name != null && !name.isBlank())
                .toList();
    }

    default List<String> extractMovieTypeNamesEn(List<MovieType> movieTypes) {
        return movieTypes == null ? List.of() : movieTypes.stream()
                .map(MovieType::getMovieTypeNameEn)
                .filter(name -> name != null && !name.isBlank())
                .toList();
    }

    default String joinMovieTypeNamesVi(List<MovieType> movieTypes) {
        if (movieTypes == null || movieTypes.isEmpty()) {
            return null;
        }
        return movieTypes.stream()
                .map(MovieType::getMovieTypeNameVi)
                .filter(name -> name != null && !name.isBlank())
                .reduce((left, right) -> left + ", " + right)
                .orElse(null);
    }

    default String joinMovieTypeNamesEn(List<MovieType> movieTypes) {
        if (movieTypes == null || movieTypes.isEmpty()) {
            return null;
        }
        return movieTypes.stream()
                .map(MovieType::getMovieTypeNameEn)
                .filter(name -> name != null && !name.isBlank())
                .reduce((left, right) -> left + ", " + right)
                .orElse(null);
    }
}
