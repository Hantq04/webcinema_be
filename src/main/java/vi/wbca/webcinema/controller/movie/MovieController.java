package vi.wbca.webcinema.controller.movie;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.model.dto.movie.MovieDTO;
import vi.wbca.webcinema.model.dto.movie.MovieNowShowingDTO;
import vi.wbca.webcinema.model.dto.movie.MovieResponseDTO;
import vi.wbca.webcinema.model.dto.movie.MovieStatisticDTO;
import vi.wbca.webcinema.validation.groupValidate.movie.InsertMovie;
import vi.wbca.webcinema.validation.groupValidate.movie.UpdateMovie;
import vi.wbca.webcinema.service.MovieService;
import vi.wbca.webcinema.util.Constants;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/movie")
public class MovieController {
    private static final Logger logger = Logger.getLogger(MovieController.class.getName());
    private final MovieService movieService;
    private final MessageSource messageSource;

    @PostMapping("/save")
    @PreAuthorize(Constants.PERM_STAFF_ADMIN)
    @Operation(summary = "Thêm phim mới")
    public ResponseEntity<ResponseObject> insertMovie(@Validated(InsertMovie.class) @RequestBody MovieDTO request) {
        logger.info("----------Web Cinema: Insert New Movie----------");
        movieService.insertMovie(request);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.insert_movie", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, null)
        );
    }

    @PutMapping("/update")
    @PreAuthorize(Constants.PERM_STAFF_ADMIN)
    @Operation(summary = "Cập nhật phim")
    public ResponseEntity<ResponseObject> updateMovie(@Validated(UpdateMovie.class) @RequestBody MovieDTO request) {
        logger.info("----------Web Cinema: Update Movie----------");
        movieService.updateMovie(request);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.update", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, null)
        );
    }

    @DeleteMapping("/delete")
    @PreAuthorize(Constants.PERM_ADMIN_ONLY)
    @Operation(summary = "Xóa phim theo tên")
    public ResponseEntity<ResponseObject> deleteMovie(@Valid @RequestParam String name) {
        logger.info("----------Web Cinema: Delete Movie----------");
        movieService.deleteMovie(name);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.delete", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, "")
        );
    }

    @GetMapping("/get-movie-page")
    @Operation(summary = "Lấy danh sách phim theo phân trang")
    public ResponseEntity<ResponseObject> getMoviePage(@RequestParam int page, @RequestParam int size) {
        logger.info("----------Web Cinema: Movie Page----------");
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.get_movie_page", null, locale);
        Pageable pageable = PageRequest.of(page, size);
        Page<MovieDTO> pageData = movieService.getMoviePage(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, pageData)
        );
    }

    @GetMapping("/sort-movie")
    @Operation(summary = "Lấy danh sách phim theo số vé bán")
    public ResponseEntity<ResponseObject> sortMovieByTicketOrder(@RequestParam int page, @RequestParam int size) {
        logger.info("----------Web Cinema: Sort Movie Page----------");
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.get_sort_movie_page", null, locale);
        Pageable pageable = PageRequest.of(page, size);
        Page<MovieStatisticDTO> pageData = movieService.sortMovieByTicketOrder(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, pageData)
        );
    }

    @GetMapping("/get-now-showing-movie")
    @Operation(summary = "Lấy danh sách phim đang chiếu")
    public ResponseEntity<ResponseObject> getNowShowingMovie() {
        logger.info("----------Web Cinema: Get Now Showing Movie----------");
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.get_now_showing_movie", null, locale);
        List<MovieNowShowingDTO> responseData = movieService.getNowShowingMovies();
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }

    @GetMapping("/get-movie-with-cinema")
    @Operation(summary = "Lấy danh sách phim theo mã rạp")
    public ResponseEntity<ResponseObject> getMovieWithCinema(@RequestParam String code, @RequestParam int page, @RequestParam int size) {
        logger.info("----------Web Cinema: Movie With Cinema Page----------");
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.get_movie_cinema", null, locale);
        Pageable pageable = PageRequest.of(page, size);
        Page<MovieResponseDTO> pageData = movieService.getMovieWithCinemaId(code, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, pageData)
        );
    }

    @GetMapping("/get-movie-with-room")
    @Operation(summary = "Lấy danh sách phim theo mã rạp và phòng")
    public ResponseEntity<ResponseObject> getMovieWithRoom(@RequestParam String cinemaCode,@RequestParam String roomCode, @RequestParam int page, @RequestParam int size) {
        logger.info("----------Web Cinema: Movie With Room Page----------");
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.get_movie_room", null, locale);
        Pageable pageable = PageRequest.of(page, size);
        Page<MovieResponseDTO> pageData = movieService.getMovieWithRoomId(cinemaCode, roomCode, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, pageData)
        );
    }

    @GetMapping("/get-movie-with-seat-status")
    @Operation(summary = "Lấy danh sách phim theo trạng thái ghế")
    public ResponseEntity<ResponseObject> getMovieWithSeaStatus(@RequestParam String seatStatus, @RequestParam int page, @RequestParam int size) {
        logger.info("----------Web Cinema: Movie With Seat Status Page----------");
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.get_movie_seat_status", null, locale);
        Pageable pageable = PageRequest.of(page, size);
        Page<MovieResponseDTO> pageData = movieService.getMovieWithSeatStatusId(seatStatus, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, pageData)
        );
    }
}
