package vi.wbca.webcinema.controller.seat;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.model.entity.seat.SeatType;
import vi.wbca.webcinema.service.SeatTypeService;
import vi.wbca.webcinema.util.Constants;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/seat/type")
public class SeatTypeController {
    private static final Logger logger = Logger.getLogger(SeatTypeController.class.getName());
    private final SeatTypeService seatTypeService;
    private final MessageSource messageSource;

    @PostMapping("/insert")
    @PreAuthorize("hasRole('" + Constants.USER + "') or hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> insertSeatType(@Valid @RequestBody SeatType seatType) {
        logger.info("----------Web Cinema: Insert New Seat Type----------");
        SeatType responseData = seatTypeService.insertSeatType(seatType);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.insert_seat_type", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }

    @GetMapping("/get-all-type")
    @PreAuthorize("hasRole('" + Constants.USER + "') or hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> getAllType() {
        logger.info("----------Web Cinema: Get All Seat Type----------");
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.get_all_seat_type", null, locale);
        List<SeatType> responseData = seatTypeService.getAllType();
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }
}
