package vi.wbca.webcinema.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vi.wbca.webcinema.model.dto.movie.MovieDTO;
import vi.wbca.webcinema.model.dto.movie.MovieNowShowingDTO;
import vi.wbca.webcinema.model.dto.movie.MovieResponseDTO;
import vi.wbca.webcinema.model.dto.movie.MovieStatisticDTO;
import vi.wbca.webcinema.model.response.MovieShowingResponse;

import java.util.List;

public interface MovieService {
    void insertMovie(MovieDTO movieDTO);

    void updateMovie(MovieDTO movieDTO);

    void deleteMovie(String name);

    Page<MovieDTO> getMoviePage(Pageable pageable);

    Page<MovieStatisticDTO> sortMovieByTicketOrder(Pageable pageable);

    List<MovieNowShowingDTO> getNowShowingMovies();

    List<MovieShowingResponse> getHotNowShowingMovies();

    List<MovieShowingResponse> getComingSoonMovies();

    Page<MovieResponseDTO> getMovieWithCinemaId(String code, Pageable pageable);

    Page<MovieResponseDTO> getMovieWithRoomId(String cinemaCode, String code, Pageable pageable);

    Page<MovieResponseDTO> getMovieWithSeatStatusId(String seatStatus, Pageable pageable);
}
