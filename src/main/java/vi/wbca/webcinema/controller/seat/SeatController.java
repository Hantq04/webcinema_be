package vi.wbca.webcinema.controller.seat;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.dto.SeatDTO;
import vi.wbca.webcinema.service.seatService.SeatService;
import vi.wbca.webcinema.util.Informations;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/seat")
public class SeatController {
    private static final Logger logger = Logger.getLogger(SeatController.class.getName());
    private final SeatService seatService;

    @PostMapping("/insert")
    public ResponseEntity<ResponseObject> insertSeat(@Valid @RequestBody SeatDTO request) {

        logger.info("----------Web Cinema: Insert New Seat----------");

        SeatDTO responseData = seatService.insertSeat(request);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Insert seat successfully.", responseData)
        );
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseObject> updateSeat(@Valid @RequestBody SeatDTO request) {

        logger.info("----------Web Cinema: Insert New Seat----------");

        seatService.updateSeat(request);

        Map<String, String> responseData = new HashMap<>();
        responseData.put(Informations.LINE, request.getLine());
        responseData.put(Informations.NUMBER, request.getNumber().toString());
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Updated seat successfully.", responseData)
        );
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ResponseObject> deleteSeat(@Valid @RequestParam Long id) {

        logger.info("----------Web Cinema: Delete Seat----------");

        seatService.deleteSeat(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Deleted seat successfully.", "")
        );
    }
}
