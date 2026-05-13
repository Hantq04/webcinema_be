package vi.wbca.webcinema.controller.cinema;

import io.swagger.v3.oas.annotations.Operation;
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
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/room")
public class RoomController {
    private static final Logger logger = Logger.getLogger(RoomController.class.getName());
    private final RoomService roomService;
    private final MessageSource messageSource;

    @PostMapping("/save")
    @PreAuthorize(Constants.PERM_STAFF_ADMIN)
    @Operation(summary = "Thêm phòng chiếu mới")
    public ResponseEntity<ResponseObject> insertRoom(@Valid @RequestBody RoomDTO request) {
        logger.info("----------Web Cinema: Insert New Room----------");
        RoomDTO responseData = roomService.insertRoom(request);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.insert_room", null, locale);
        return ResponseEntity.status(HttpStatus.CREATED).body(
            new ResponseObject(HttpStatus.CREATED, message, responseData)
        );
    }

    @PutMapping("/update")
    @PreAuthorize(Constants.PERM_STAFF_ADMIN)
    @Operation(summary = "Cập nhật phòng chiếu")
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
    @Operation(summary = "Xóa phòng chiếu theo mã")
    public ResponseEntity<ResponseObject> deleteRoom(@Valid @RequestParam String code) {
        logger.info("----------Web Cinema: Delete Room----------");
        roomService.deleteRoom(code);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.delete", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, "")
        );
    }

    @GetMapping("/get-by-cinema")
    @Operation(summary = "Lấy danh sách mã phòng theo rạp")
    public ResponseEntity<ResponseObject> getRoomsByCinema(@RequestParam String cinemaName) {
        logger.info("----------Web Cinema: Get Rooms By Cinema: " + cinemaName + "----------");
        List<String> responseData = roomService.getRoomCodesByCinema(cinemaName);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.get_room_by_cinema", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }

    @GetMapping("/get-by-cinema-detail")
    @Operation(summary = "Lấy danh sách phòng đầy đủ theo rạp")
    public ResponseEntity<ResponseObject> getRoomsDetailByCinema(@RequestParam String cinemaName) {
        logger.info("----------Web Cinema: Get Rooms Detail By Cinema: " + cinemaName + "----------");
        List<RoomDTO> responseData = roomService.getRoomsByCinema(cinemaName);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.get_room_by_cinema_detail", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }
}
