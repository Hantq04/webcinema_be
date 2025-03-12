package vi.wbca.webcinema.controller.movie;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.model.MovieType;
import vi.wbca.webcinema.service.movieTypeService.MovieTypeService;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/movie/type")
public class MovieTypeController {
    private static final Logger logger = Logger.getLogger(MovieTypeController.class.getName());
    private final MovieTypeService movieTypeService;

    @PostMapping("/insert")
    public ResponseEntity<ResponseObject> insertMovieType(@Valid @RequestBody MovieType request) {

        logger.info("----------Web Cinema: Insert New Movie Type----------");

        MovieType responseData = movieTypeService.insertMovieType(request);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Insert room successfully.", responseData)
        );
    }

    @GetMapping("/get-all-type")
    public List<MovieType> getAllType() {

        logger.info("----------Web Cinema: Get All Movie Type----------");

        return movieTypeService.getAllType();
    }
}
