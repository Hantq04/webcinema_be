package vi.wbca.webcinema.controller.movie;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.model.entity.movie.MovieType;
import vi.wbca.webcinema.model.response.MovieTypeResponse;
import vi.wbca.webcinema.service.MovieTypeService;
import vi.wbca.webcinema.util.Constants;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/movie/type")
public class MovieTypeController {
    private static final Logger logger = Logger.getLogger(MovieTypeController.class.getName());
    private final MovieTypeService movieTypeService;
    private final MessageSource messageSource;

    @PostMapping("/save")
    @PreAuthorize(Constants.PERM_STAFF_ADMIN)
    @Operation(summary = "Thêm thể loại phim mới")
    public ResponseEntity<ResponseObject> insertMovieType(@Valid @RequestBody MovieType request) {
        logger.info("----------Web Cinema: Insert New Movie Type----------");
        MovieType responseData = movieTypeService.insertMovieType(request);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.insert_movie_type", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }

    @GetMapping("/get-all-type")
    @PreAuthorize(Constants.PERM_STAFF_ADMIN)
    @Operation(summary = "Lấy tất cả thể loại phim")
    public ResponseEntity<ResponseObject> getAllType() {
        logger.info("----------Web Cinema: Get All Movie Type----------");
        List<MovieTypeResponse> responseData = movieTypeService.getAllType();
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.get_all_movie_type", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }
}
