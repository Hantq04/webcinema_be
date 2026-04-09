package vi.wbca.webcinema.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.model.dto.movie.MovieDTO;
import vi.wbca.webcinema.model.dto.movie.MovieResponseDTO;
import vi.wbca.webcinema.model.dto.movie.MovieStatisticDTO;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.mapper.MovieMapper;
import vi.wbca.webcinema.model.entity.cinema.Cinema;
import vi.wbca.webcinema.model.entity.cinema.Room;
import vi.wbca.webcinema.model.entity.movie.Movie;
import vi.wbca.webcinema.model.entity.movie.MovieType;
import vi.wbca.webcinema.model.entity.movie.Rate;
import vi.wbca.webcinema.model.entity.seat.SeatStatus;
import vi.wbca.webcinema.repository.cinema.CinemaRepo;
import vi.wbca.webcinema.repository.cinema.RoomRepo;
import vi.wbca.webcinema.repository.movie.MovieRepo;
import vi.wbca.webcinema.repository.movie.MovieTypeRepo;
import vi.wbca.webcinema.repository.movie.RateRepo;
import vi.wbca.webcinema.repository.seat.SeatStatusRepo;
import vi.wbca.webcinema.service.MovieService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {
    private final MovieRepo movieRepo;
    private final MovieMapper movieMapper;
    private final MovieTypeRepo movieTypeRepo;
    private final RateRepo rateRepo;
    private final CinemaRepo cinemaRepo;
    private final RoomRepo roomRepo;
    private final SeatStatusRepo seatStatusRepo;

    @Override
    public void insertMovie(MovieDTO request) {
        Movie movie = movieMapper.toMovie(request);
        MovieType movieType = movieTypeRepo.findByMovieTypeName(request.getMovieTypeName())
                .orElseThrow(() -> new AppException(ErrorCode.TYPE_NOT_FOUND));

        LocalDateTime premiereDate = request.getPremiereDate();
        movie.setMovieType(movieType);
        movie.setRate(setRate(request));
        movie.setPremiereDate(premiereDate);
        movie.setEndDate(premiereDate.plusDays(30));
        movie.setActive(true);
        movieRepo.save(movie);
    }

    @Override
    public void updateMovie(MovieDTO movieDTO) {
        Movie movie = movieRepo.findByNameAndIsActive(movieDTO.getName(), true)
                .orElseThrow(() -> new AppException(ErrorCode.NAME_NOT_FOUND));

        movie.setMovieDuration(movieDTO.getMovieDuration());
        movie.setDescription(movieDTO.getDescription());
        movie.setDirector(movieDTO.getDirector());
        movie.setImage(movieDTO.getImage());
        movie.setLanguage(movieDTO.getLanguage());
        movie.setTrailer(movieDTO.getTrailer());
        movie.setRate(setRate(movieDTO));
        movieRepo.save(movie);
    }

    @Override
    public void deleteMovie(String name) {
        Movie movie = movieRepo.findByNameAndIsActive(name, true)
                .orElseThrow(() -> new AppException(ErrorCode.NAME_NOT_FOUND));
        movie.setActive(false);
        movieRepo.save(movie);
    }

    @Override
    public Page<MovieDTO> getMoviePage(Pageable pageable) {
        Page<Movie> movies = movieRepo.findAll(pageable);
        return movies.map(movieMapper::toMovieDTO);
    }

    @Override
    public Page<MovieStatisticDTO> sortMovieByTicketOrder(Pageable pageable) {
        return movieRepo.getTicketStatistics(pageable);
    }

    @Override
    public Page<MovieResponseDTO> getMovieWithCinemaId(String code, Pageable pageable) {
        Cinema cinema = getCinema(code);
        return movieRepo.getMovieWithCinema(cinema.getId(), pageable);
    }

    @Override
    public Page<MovieResponseDTO> getMovieWithRoomId(String cinemaCode, String code, Pageable pageable) {
        Cinema cinema = getCinema(cinemaCode);
        Room room = roomRepo.findByCodeAndCinema(code, cinema)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
        return movieRepo.getMovieWithRoom(room.getId(), pageable);
    }

    @Override
    public Page<MovieResponseDTO> getMovieWithSeatStatusId(String name, Pageable pageable) {
        SeatStatus seatStatus = seatStatusRepo.findByCode(name)
                .orElseThrow(() -> new AppException(ErrorCode.STATUS_NOT_FOUND));
        return movieRepo.getMovieWithSeatStatus(seatStatus.getId(), pageable);
    }

    public Rate setRate(MovieDTO movieDTO) {
        return rateRepo.findByCode(movieDTO.getCode())
                .orElseThrow(() -> new AppException(ErrorCode.RATE_NOT_FOUND));
    }

    public Cinema getCinema(String code) {
        return cinemaRepo.findByCode(code).
                orElseThrow(() -> new AppException(ErrorCode.CODE_NOT_FOUND));
    }
}
