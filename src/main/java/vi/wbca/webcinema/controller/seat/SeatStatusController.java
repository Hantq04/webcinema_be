package vi.wbca.webcinema.controller.seat;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.model.entity.seat.SeatStatus;
import vi.wbca.webcinema.service.SeatStatusService;
import vi.wbca.webcinema.util.Constants;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/seat/status")
public class SeatStatusController {
    private static final Logger logger = Logger.getLogger(SeatStatusController.class.getName());
    private final SeatStatusService seatStatusService;
    private final MessageSource messageSource;

    @PostMapping("/insert")
    @PreAuthorize(Constants.PERM_STAFF_ADMIN)
    public ResponseEntity<ResponseObject> insertSeatStatus(@Valid @RequestBody SeatStatus seatStatus) {
        logger.info("----------Web Cinema: Insert New Seat Status----------");
        SeatStatus response = seatStatusService.insertSeatStatus(seatStatus);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.insert_seat_status", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, response)
        );
    }

    @GetMapping("/get-all-status")
    @PreAuthorize(Constants.PERM_STAFF_ADMIN)
    public ResponseEntity<ResponseObject> getAllStatus() {
        logger.info("----------Web Cinema: Get All Seat Status----------");
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.get_all_seat_status", null, locale);
        List<SeatStatus> responseData = seatStatusService.getAllStatus();
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }
}
