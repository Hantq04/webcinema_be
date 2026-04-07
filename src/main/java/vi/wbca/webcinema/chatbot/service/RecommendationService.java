package vi.wbca.webcinema.chatbot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.model.entity.movie.Movie;
import vi.wbca.webcinema.repository.movie.MovieRepo;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final MovieRepo movieRepo;

//    public List<Movie> getHotMovies() {
//        return movieRepo.findHotMovies(PageRequest.of(0, 5));
//    }
}
