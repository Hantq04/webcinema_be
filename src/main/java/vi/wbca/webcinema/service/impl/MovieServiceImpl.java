package vi.wbca.webcinema.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.model.dto.movie.MovieDTO;
import vi.wbca.webcinema.model.dto.movie.MovieNowShowingDTO;
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
import vi.wbca.webcinema.model.entity.setting.Banner;
import vi.wbca.webcinema.model.entity.seat.SeatStatus;
import vi.wbca.webcinema.model.response.MovieDetailResponse;
import vi.wbca.webcinema.model.response.MovieShowingResponse;
import vi.wbca.webcinema.repository.cinema.CinemaRepo;
import vi.wbca.webcinema.repository.cinema.RoomRepo;
import vi.wbca.webcinema.repository.movie.MovieRepo;
import vi.wbca.webcinema.repository.movie.MovieTypeRepo;
import vi.wbca.webcinema.repository.movie.RateRepo;
import vi.wbca.webcinema.repository.setting.BannerRepo;
import vi.wbca.webcinema.repository.seat.SeatStatusRepo;
import vi.wbca.webcinema.service.MovieService;
import vi.wbca.webcinema.util.generate.GenerateCode;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {
    private final MovieRepo movieRepo;
    private final MovieMapper movieMapper;
    private final MovieTypeRepo movieTypeRepo;
    private final RateRepo rateRepo;
    private final BannerRepo bannerRepo;
    private final CinemaRepo cinemaRepo;
    private final RoomRepo roomRepo;
    private final SeatStatusRepo seatStatusRepo;

    @Override
    public void insertMovie(MovieDTO request) {
        Movie movie = movieMapper.toMovie(request);
        List<MovieType> movieTypes = resolveMovieTypes(request.getMovieTypeIds());

        LocalDateTime premiereDate = request.getPremiereDate();
        movie.setCode(GenerateCode.generateCode());
        movie.setNameEn(request.getNameEn());
        movie.setDescriptionEn(request.getDescriptionEn());
        movie.setMovieTypes(movieTypes);
        movie.setRate(setRate(request));
        movie.setPremiereDate(premiereDate);
        movie.setEndDate(premiereDate.plusDays(30));
        applyBanner(movie, request.getBannerId());
        movie.setActive(true);
        movieRepo.save(movie);
    }

    @Override
    public void updateMovie(MovieDTO movieDTO) {
        Movie movie = movieRepo.findByNameAndIsActive(movieDTO.getName(), true)
                .orElseThrow(() -> new AppException(ErrorCode.NAME_NOT_FOUND));

        movie.setMovieDuration(movieDTO.getMovieDuration());
        movie.setDescription(movieDTO.getDescription());
        movie.setDescriptionEn(movieDTO.getDescriptionEn());
        movie.setDirector(movieDTO.getDirector());
        movie.setActor(movieDTO.getActor());
        movie.setNameEn(movieDTO.getNameEn());
        movie.setMovieTypes(resolveMovieTypes(movieDTO.getMovieTypeIds()));
        movie.setPremiereDate(movieDTO.getPremiereDate());
        movie.setEndDate(movieDTO.getEndDate());
        movie.setLanguage(movieDTO.getLanguage());
        movie.setSubtitle(movieDTO.getSubtitle());
        movie.setTrailer(movieDTO.getTrailer());
        movie.setCode(movieDTO.getCode());
        movie.setRate(setRate(movieDTO));
        applyBanner(movie, movieDTO.getBannerId());
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
    public MovieDetailResponse getMovieDetailByCode(String code) {
        Movie movie = movieRepo.findByCodeAndIsActive(code, true)
                .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_FOUND));
        return movieMapper.toMovieDetailResponse(movie);
    }

    @Override
    public Page<MovieStatisticDTO> sortMovieByTicketOrder(Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();
        return movieRepo.getTicketStatistics(now, pageable).map(movie -> {
            MovieStatisticDTO dto = movieMapper.toMovieStatisticDTO(movie);
            dto.setTotalTicketsBooked(movieRepo.countTotalTicketsByMovieId(movie.getId()));
            return dto;
        });
    }

    @Override
    public List<MovieNowShowingDTO> getNowShowingMovies() {
        LocalDateTime now = LocalDateTime.now();
        return movieRepo.filterMovies(now, true, false, null).stream()
            .map(movieMapper::toMovieNowShowingDTO)
            .toList();
    }

    @Override
    public List<MovieShowingResponse> getHotNowShowingMovies() {
        LocalDateTime now = LocalDateTime.now();
        return movieRepo.findHotNowShowingMovies(now).stream()
            .map(movieMapper::toMovieShowingResponse)
            .toList();
    }

    @Override
    public List<MovieShowingResponse> getComingSoonMovies() {
        LocalDateTime now = LocalDateTime.now();
        return movieRepo.filterMovies(now, false, true, null).stream()
            .map(movieMapper::toMovieShowingResponse)
            .toList();
    }

    @Override
    public Page<MovieResponseDTO> getMovieWithCinemaId(String code, Pageable pageable) {
        Cinema cinema = getCinema(code);
        return movieRepo.getMovieWithCinema(cinema.getId(), pageable).map(movieMapper::toMovieResponseDTO);
    }

    @Override
    public Page<MovieResponseDTO> getMovieWithRoomId(String cinemaCode, String code, Pageable pageable) {
        Cinema cinema = getCinema(cinemaCode);
        Room room = roomRepo.findByCodeAndCinema(code, cinema)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
        return movieRepo.getMovieWithRoom(room.getId(), pageable).map(movieMapper::toMovieResponseDTO);
    }

    @Override
    public Page<MovieResponseDTO> getMovieWithSeatStatusId(String name, Pageable pageable) {
        SeatStatus seatStatus = seatStatusRepo.findByCode(name)
                .orElseThrow(() -> new AppException(ErrorCode.STATUS_NOT_FOUND));
        return movieRepo.getMovieWithSeatStatus(seatStatus.getId(), pageable).map(movieMapper::toMovieResponseDTO);
    }

    public Rate setRate(MovieDTO movieDTO) {
        return rateRepo.findByCode(movieDTO.getRate())
                .orElseThrow(() -> new AppException(ErrorCode.RATE_NOT_FOUND));
    }

    public Cinema getCinema(String code) {
        return cinemaRepo.findByCode(code).
                orElseThrow(() -> new AppException(ErrorCode.CODE_NOT_FOUND));
    }

    private void applyBanner(Movie movie, Long bannerId) {
        Banner banner = bannerRepo.findById(bannerId)
                .orElseThrow(() -> new AppException(ErrorCode.ID_NOT_FOUND));
        movie.setBanner(banner);
        movie.setImage(banner.getImageUrl());
    }

    private List<MovieType> resolveMovieTypes(List<Long> movieTypeIds) {
        if (movieTypeIds == null || movieTypeIds.isEmpty()) {
            throw new AppException(ErrorCode.TYPE_NOT_FOUND);
        }

        List<MovieType> movieTypes = movieTypeRepo.findByIdInAndIsActiveTrue(movieTypeIds);
        if (movieTypes.size() != movieTypeIds.size()) {
            throw new AppException(ErrorCode.TYPE_NOT_FOUND);
        }

        return movieTypeIds.stream()
                .map(id -> movieTypes.stream()
                        .filter(movieType -> movieType.getId().equals(id))
                        .findFirst()
                        .orElseThrow(() -> new AppException(ErrorCode.TYPE_NOT_FOUND)))
                .toList();
    }
}
