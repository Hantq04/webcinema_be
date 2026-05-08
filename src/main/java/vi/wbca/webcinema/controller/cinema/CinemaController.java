package vi.wbca.webcinema.controller.cinema;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.model.dto.cinema.CinemaDTO;
import vi.wbca.webcinema.validation.groupValidate.cinema.DeleteCinema;
import vi.wbca.webcinema.validation.groupValidate.cinema.InsertCinema;
import vi.wbca.webcinema.validation.groupValidate.cinema.UpdateCinema;
import vi.wbca.webcinema.service.CinemaService;
import vi.wbca.webcinema.util.Constants;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.Locale;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cinema")
public class CinemaController {
    private static final Logger logger = Logger.getLogger(CinemaController.class.getName());
    private final CinemaService cinemaService;
    private final MessageSource messageSource;

    @PostMapping("/save")
    @PreAuthorize(Constants.PERM_STAFF_ADMIN)
    @Operation(summary = "Thêm rạp chiếu mới")
    public ResponseEntity<ResponseObject> insertCinema(@Validated(InsertCinema.class) @RequestBody CinemaDTO request) {
        logger.info("----------Web Cinema: Insert New Cinema----------");
        CinemaDTO responseData = cinemaService.insertCinema(request);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.insert_cinema", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }

    @PutMapping("/update")
    @PreAuthorize(Constants.PERM_STAFF_ADMIN)
    @Operation(summary = "Cập nhật rạp chiếu")
    public ResponseEntity<ResponseObject> updateCinema(@Validated(UpdateCinema.class) @RequestBody CinemaDTO request) {
        logger.info("----------Web Cinema: Update Cinema----------");
        cinemaService.updateCinema(request);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.update", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, null)
        );
    }

    @DeleteMapping("/delete")
    @PreAuthorize(Constants.PERM_ADMIN_ONLY)
    @Operation(summary = "Xóa rạp chiếu theo mã")
    public ResponseEntity<ResponseObject> deleteCinema(@Validated(DeleteCinema.class) @RequestParam String code) {
        logger.info("----------Web Cinema: Delete Cinema----------");
        cinemaService.deleteCinema(code);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.delete", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, "")
        );
    }

    @GetMapping("/get-all-address")
    @Operation(summary = "Lấy danh sách tất cả địa chỉ rạp đang hoạt động")
    public ResponseEntity<ResponseObject> getAllAddressActive() {
        logger.info("----------Web Cinema: Get All Cinema Address Active----------");
        List<String> responseData = cinemaService.getAllAddressActive();
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.get_all_cinema_address", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }

    @GetMapping("/get-name-by-address")
    @Operation(summary = "Lấy danh sách tên rạp theo địa chỉ")
    public ResponseEntity<ResponseObject> getCinemaNamesByAddress(@RequestParam String address) {
        logger.info("----------Web Cinema: Get Cinema Names By Address----------");
        List<String> responseData = cinemaService.getCinemaNamesByAddress(address);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.get_cinema_names_by_address", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }

    @GetMapping("/get-all-cinema")
    @Operation(summary = "Lấy danh sách tất cả rạp")
    public ResponseEntity<ResponseObject> getAllCinema() {
        logger.info("----------Web Cinema: Get All Cinema----------");
        List<CinemaDTO> responseData = cinemaService.getAllCinema();
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.get_all_cinema", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }
}
