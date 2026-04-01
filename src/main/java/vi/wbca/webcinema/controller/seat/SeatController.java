package vi.wbca.webcinema.controller.seat;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.model.dto.room.SeatDTO;
import vi.wbca.webcinema.model.response.SeatResponse;
import vi.wbca.webcinema.validation.groupValidate.seat.InsertSeat;
import vi.wbca.webcinema.validation.groupValidate.seat.UpdateSeat;
import vi.wbca.webcinema.model.entity.seat.Seat;
import vi.wbca.webcinema.service.SeatService;
import vi.wbca.webcinema.util.Constants;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/seat")
public class SeatController {
    private static final Logger logger = Logger.getLogger(SeatController.class.getName());
    private final SeatService seatService;
    private final MessageSource messageSource;

    @PostMapping("/insert")
    @PreAuthorize("hasRole('" + Constants.USER + "') or hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> insertSeat(@Validated(InsertSeat.class) @RequestBody SeatDTO request) {
        logger.info("----------Web Cinema: Insert New Seat----------");
        seatService.insertSeat(request);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.insert_seat", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, null)
        );
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('" + Constants.USER + "') or hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> updateSeat(@Validated(UpdateSeat.class) @RequestBody SeatDTO request) {
        logger.info("----------Web Cinema: Update Seat----------");
        seatService.updateSeat(request);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.update", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, null)
        );
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> deleteSeat(@Valid @RequestParam Long id) {
        logger.info("----------Web Cinema: Delete Seat----------");
        seatService.deleteSeat(id);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.delete", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, "")
        );
    }

    @PutMapping("/refresh")
    @PreAuthorize("hasRole('" + Constants.USER + "') or hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> refreshSeat(@RequestParam String code) {
        logger.info("----------Web Cinema: Update Seat Status----------");
        seatService.refreshSeat(code);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.refresh_seat", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, "")
        );
    }

    @GetMapping("/get-all-seat")
    @PreAuthorize("hasRole('" + Constants.USER + "') or hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> getAllSeat() {
        logger.info("----------Web Cinema: Get All Seat----------");
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.get_all_seat", null, locale);
        List<SeatResponse> responseData = seatService.getAllSeat();
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }
}
