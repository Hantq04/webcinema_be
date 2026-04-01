package vi.wbca.webcinema.controller.cinema;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.model.dto.room.RoomDTO;
import vi.wbca.webcinema.service.RoomService;
import vi.wbca.webcinema.util.Constants;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.Locale;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/room")
public class RoomController {
    private static final Logger logger = Logger.getLogger(RoomController.class.getName());
    private final RoomService roomService;
    private final MessageSource messageSource;

    @PostMapping("/insert")
    @PreAuthorize(Constants.PERM_STAFF_ADMIN)
    public ResponseEntity<ResponseObject> insertRoom(@Valid @RequestBody RoomDTO request) {
        logger.info("----------Web Cinema: Insert New Room----------");
        RoomDTO responseData = roomService.insertRoom(request);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.insert_room", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }

    @PutMapping("/update")
    @PreAuthorize(Constants.PERM_STAFF_ADMIN)
    public ResponseEntity<ResponseObject> updateRoom(@Valid @RequestBody RoomDTO request) {
        logger.info("----------Web Cinema: Update Room----------");
        roomService.updateRoom(request);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.update", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, null)
        );
    }

    @DeleteMapping("/delete")
    @PreAuthorize(Constants.PERM_ADMIN_ONLY)
    public ResponseEntity<ResponseObject> deleteRoom(@Valid @RequestParam String code) {
        logger.info("----------Web Cinema: Delete Room----------");
        roomService.deleteRoom(code);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.delete", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, "")
        );
    }
}
