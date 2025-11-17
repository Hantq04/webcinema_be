package vi.wbca.webcinema.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.dto.movie.MovieDTO;
import vi.wbca.webcinema.dto.movie.MovieResponseDTO;
import vi.wbca.webcinema.dto.movie.MovieStatisticDTO;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.mapper.MovieMapper;
import vi.wbca.webcinema.model.cinema.Cinema;
import vi.wbca.webcinema.model.cinema.Room;
import vi.wbca.webcinema.model.movie.Movie;
import vi.wbca.webcinema.model.movie.MovieType;
import vi.wbca.webcinema.model.movie.Rate;
import vi.wbca.webcinema.model.seat.SeatStatus;
import vi.wbca.webcinema.repository.cinema.CinemaRepo;
import vi.wbca.webcinema.repository.cinema.RoomRepo;
import vi.wbca.webcinema.repository.movie.MovieRepo;
import vi.wbca.webcinema.repository.movie.MovieTypeRepo;
import vi.wbca.webcinema.repository.movie.RateRepo;
import vi.wbca.webcinema.repository.seat.SeatStatusRepo;
import vi.wbca.webcinema.service.MovieService;

import java.util.Calendar;

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
    public MovieDTO insertMovie(MovieDTO movieDTO) {
        Movie movie = movieMapper.toMovie(movieDTO);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(movie.getPremiereDate());
        calendar.add(Calendar.DAY_OF_MONTH, 30);
        movie.setEndDate(calendar.getTime());
        movie.setActive(true);
        movie.setMovieType(setType(movieDTO));
        movie.setRate(setRate(movieDTO));

        movieRepo.save(movie);
        return movieMapper.toMovieDTO(movie);
    }

    @Override
    public void updateMovie(MovieDTO movieDTO) {
        Movie movie = movieRepo.findByName(movieDTO.getName())
                .orElseThrow(() -> new AppException(ErrorCode.NAME_NOT_FOUND));

        movie.setMovieDuration(movieDTO.getMovieDuration());
        movie.setDescription(movieDTO.getDescription());
        movie.setDirector(movieDTO.getDirector());
        movie.setImage(movieDTO.getImage());
        movie.setHeroImage(movieDTO.getHeroImage());
        movie.setLanguage(movieDTO.getLanguage());
        movie.setTrailer(movieDTO.getTrailer());
        movie.setRate(setRate(movieDTO));

        movieRepo.save(movie);
    }

    public MovieType setType(MovieDTO movieDTO) {
        return movieTypeRepo.findByMovieTypeName(movieDTO.getMovieTypeName())
                .orElseThrow(() -> new AppException(ErrorCode.TYPE_NOT_FOUND));
    }

    public Rate setRate(MovieDTO movieDTO) {
        return rateRepo.findByCode(movieDTO.getCode())
                .orElseThrow(() -> new AppException(ErrorCode.RATE_NOT_FOUND));
    }

    @Override
    public void deleteMovie(String name) {
        Movie movie = movieRepo.findByName(name)
                .orElseThrow(() -> new AppException(ErrorCode.NAME_NOT_FOUND));
        movieRepo.delete(movie);
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

    public Cinema getCinema(String code) {
        return cinemaRepo.findByCode(code).
                orElseThrow(() -> new AppException(ErrorCode.CODE_NOT_FOUND));
    }
}
