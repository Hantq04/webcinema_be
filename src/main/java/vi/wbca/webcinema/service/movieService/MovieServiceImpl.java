package vi.wbca.webcinema.service.movieService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.dto.MovieDTO;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.mapper.MovieMapper;
import vi.wbca.webcinema.model.Movie;
import vi.wbca.webcinema.model.MovieType;
import vi.wbca.webcinema.repository.MovieRepo;
import vi.wbca.webcinema.repository.MovieTypeRepo;

import java.util.Calendar;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService{
    private final MovieRepo movieRepo;
    private final MovieMapper movieMapper;
    private final MovieTypeRepo movieTypeRepo;

    @Override
    public MovieDTO insertMovie(MovieDTO movieDTO) {
        Movie movie = movieMapper.toMovie(movieDTO);
        MovieType movieType = movieTypeRepo.findByMovieTypeName(movieDTO.getMovieTypeName())
                .orElseThrow(() -> new AppException(ErrorCode.TYPE_NOT_FOUND));

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(movie.getPremiereDate());
        calendar.add(Calendar.DAY_OF_MONTH, 30);
        movie.setEndTime(calendar.getTime());
        movie.setActive(true);
        movie.setMovieType(movieType);

        movieRepo.save(movie);
        return movieMapper.toMovieDTO(movie);
    }

    @Override
    public void updateMovie(MovieDTO movieDTO) {
        Movie movie = movieRepo.findByName(movieDTO.getName())
                .orElseThrow(() -> new AppException(ErrorCode.NAME_NOT_FOUND));
        MovieType movieType = movieTypeRepo.findByMovieTypeName(movieDTO.getMovieTypeName())
                .orElseThrow(() -> new AppException(ErrorCode.TYPE_NOT_FOUND));

        movie.setMovieDuration(movieDTO.getMovieDuration());
        movie.setDescription(movieDTO.getDescription());
        movie.setDirector(movieDTO.getDirector());
        movie.setImage(movieDTO.getImage());
        movie.setHeroImage(movieDTO.getHeroImage());
        movie.setLanguage(movieDTO.getLanguage());
        movie.setTrailer(movieDTO.getTrailer());
        movie.setMovieType(movieType);

        movieRepo.save(movie);
    }

    @Override
    public void deleteMovie(String name) {
        Movie movie = movieRepo.findByName(name)
                .orElseThrow(() -> new AppException(ErrorCode.NAME_NOT_FOUND));
        movieRepo.delete(movie);
    }
}
