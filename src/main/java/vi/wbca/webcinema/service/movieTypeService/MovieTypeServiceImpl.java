package vi.wbca.webcinema.service.movieTypeService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.model.MovieType;
import vi.wbca.webcinema.repository.MovieTypeRepo;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieTypeServiceImpl implements MovieTypeService{
    private final MovieTypeRepo movieTypeRepo;

    @Override
    public MovieType insertMovieType(MovieType movieType) {
        movieType.setActive(true);
        return movieTypeRepo.save(movieType);
    }

    @Override
    public List<MovieType> getAllType() {
        return movieTypeRepo.findAll();
    }
}
