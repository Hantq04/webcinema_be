package vi.wbca.webcinema.controller.cinema;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.dto.room.RoomDTO;
import vi.wbca.webcinema.service.RoomService;
import vi.wbca.webcinema.util.Constants;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/room")
public class RoomController {
    private static final Logger logger = Logger.getLogger(RoomController.class.getName());
    private final RoomService roomService;

    @PostMapping("/insert")
    @PreAuthorize("hasRole('" + Constants.USER + "') or hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> insertRoom(@Valid @RequestBody RoomDTO request) {
        logger.info("----------Web Cinema: Insert New Room----------");
        RoomDTO responseData = roomService.insertRoom(request);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Insert room successfully.", responseData)
        );
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('" + Constants.USER + "') or hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> updateRoom(@Valid @RequestBody RoomDTO request) {
        logger.info("----------Web Cinema: Update Room----------");
        roomService.updateRoom(request);
        Map<String, String> responseData = new HashMap<>();
        responseData.put(Constants.CODE, request.getCode());
        responseData.put(Constants.NAME, request.getName());
        responseData.put(Constants.DESCRIPTION, request.getDescription());
        responseData.put(Constants.CAPACITY, request.getCapacity().toString());
        responseData.put(Constants.TYPE, request.getType().toString());
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Updated room successfully.", responseData)
        );
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> deleteRoom(@Valid @RequestParam String code) {
        logger.info("----------Web Cinema: Delete Room----------");
        roomService.deleteRoom(code);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Deleted room successfully.", "")
        );
    }
}
