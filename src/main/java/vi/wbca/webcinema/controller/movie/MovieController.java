package vi.wbca.webcinema.controller.movie;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.dto.MovieDTO;
import vi.wbca.webcinema.groupValidate.movie.InsertMovie;
import vi.wbca.webcinema.groupValidate.movie.UpdateMovie;
import vi.wbca.webcinema.service.movieService.MovieService;
import vi.wbca.webcinema.util.Constants;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/movie")
public class MovieController {
    private static final Logger logger = Logger.getLogger(MovieController.class.getName());
    private final MovieService movieService;

    @PostMapping("/insert")
    public ResponseEntity<ResponseObject> insertMovie(@Validated(InsertMovie.class) @RequestBody MovieDTO request) {

        logger.info("----------Web Cinema: Insert New Movie----------");

        MovieDTO responseData = movieService.insertMovie(request);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Insert movie successfully.", responseData)
        );
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseObject> updateMovie(@Validated(UpdateMovie.class) @RequestBody MovieDTO request) {

        logger.info("----------Web Cinema: Update Movie----------");

        movieService.updateMovie(request);

        Map<String, String> responseData = new HashMap<>();
        responseData.put(Constants.DURATION, request.getMovieDuration().toString());
        responseData.put(Constants.DESCRIPTION, request.getDescription());
        responseData.put(Constants.DIRECTOR, request.getDirector());
        responseData.put(Constants.IMAGE, request.getImage());
        responseData.put(Constants.HERO_IMAGE, request.getHeroImage());
        responseData.put(Constants.LANGUAGE, request.getLanguage());
        responseData.put(Constants.NAME, request.getName());
        responseData.put(Constants.TRAILER, request.getTrailer());

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Updated movie successfully.", responseData)
        );
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ResponseObject> deleteMovie(@Valid @RequestParam String name) {

        logger.info("----------Web Cinema: Delete Movie----------");

        movieService.deleteMovie(name);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Deleted movie successfully.", "")
        );
    }
}
