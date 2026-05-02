package vi.wbca.webcinema.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.model.entity.movie.MovieType;
import vi.wbca.webcinema.mapper.MovieTypeMapper;
import vi.wbca.webcinema.repository.movie.MovieTypeRepo;
import vi.wbca.webcinema.service.MovieTypeService;
import vi.wbca.webcinema.model.response.MovieTypeResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieTypeServiceImpl implements MovieTypeService {
    private final MovieTypeRepo movieTypeRepo;
    private final MovieTypeMapper movieTypeMapper;

    @Override
    public MovieType insertMovieType(MovieType movieType) {
        movieType.setActive(true);
        return movieTypeRepo.save(movieType);
    }

    @Override
    public List<MovieTypeResponse> getAllType() {
        return movieTypeRepo.findAll().stream()
                .map(movieTypeMapper::toMovieTypeResponse)
                .toList();
    }
}
