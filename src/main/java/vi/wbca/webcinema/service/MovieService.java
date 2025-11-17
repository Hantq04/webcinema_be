package vi.wbca.webcinema.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vi.wbca.webcinema.dto.movie.MovieDTO;
import vi.wbca.webcinema.dto.movie.MovieResponseDTO;
import vi.wbca.webcinema.dto.movie.MovieStatisticDTO;

public interface MovieService {
    MovieDTO insertMovie(MovieDTO movieDTO);

    void updateMovie(MovieDTO movieDTO);

    void deleteMovie(String name);

    Page<MovieDTO> getMoviePage(Pageable pageable);

    Page<MovieStatisticDTO> sortMovieByTicketOrder(Pageable pageable);

    Page<MovieResponseDTO> getMovieWithCinemaId(String code, Pageable pageable);

    Page<MovieResponseDTO> getMovieWithRoomId(String cinemaCode, String code, Pageable pageable);

    Page<MovieResponseDTO> getMovieWithSeatStatusId(String seatStatus, Pageable pageable);
}
