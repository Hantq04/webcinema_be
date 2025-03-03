package vi.wbca.webcinema.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.dto.RoomDTO;
import vi.wbca.webcinema.service.roomService.RoomService;
import vi.wbca.webcinema.util.Informations;
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
    public ResponseEntity<ResponseObject> insertRoom(@Valid @RequestBody RoomDTO request) {

        logger.info("----------Web Cinema: Insert New Room----------");

        RoomDTO responseData = roomService.insertRoom(request);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Insert room successfully.", responseData)
        );
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseObject> updateRoom(@Valid @RequestBody RoomDTO request) {

        logger.info("----------Web Cinema: Update Room----------");

        roomService.updateRoom(request);

        Map<String, String> responseData = new HashMap<>();
        responseData.put(Informations.CODE, request.getCode());
        responseData.put(Informations.NAME, request.getName());
        responseData.put(Informations.DESCRIPTION, request.getDescription());
        responseData.put(Informations.CAPACITY, request.getCapacity().toString());
        responseData.put(Informations.TYPE, request.getType().toString());

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Updated room successfully.", responseData)
        );
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ResponseObject> deleteRoom(@Valid @RequestParam String code) {

        logger.info("----------Web Cinema: Delete Room----------");

        roomService.deleteRoom(code);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Deleted room successfully.", "")
        );
    }
}
