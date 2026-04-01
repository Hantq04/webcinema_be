package vi.wbca.webcinema.controller.cinema;

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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cinema")
public class CinemaController {
    private static final Logger logger = Logger.getLogger(CinemaController.class.getName());
    private final CinemaService cinemaService;
    private final MessageSource messageSource;

    @PostMapping("/insert")
    @PreAuthorize("hasRole('" + Constants.USER + "') or hasRole('" + Constants.ADMIN + "')")
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
    @PreAuthorize("hasRole('" + Constants.USER + "') or hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> updateCinema(@Validated(UpdateCinema.class) @RequestBody CinemaDTO request) {
        logger.info("----------Web Cinema: Update Cinema----------");
        cinemaService.updateCinema(request);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.update", null, locale);
        Map<String, String> responseData = new HashMap<>();
        responseData.put(Constants.CODE, request.getCode());
        responseData.put(Constants.NAME, request.getNameOfCinema());
        responseData.put(Constants.ADDRESS, request.getAddress());
        responseData.put(Constants.DESCRIPTION, request.getDescription());
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> deleteCinema(@Validated(DeleteCinema.class) @RequestParam String code) {
        logger.info("----------Web Cinema: Delete Cinema----------");
        cinemaService.deleteCinema(code);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.delete", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, "")
        );
    }
}
