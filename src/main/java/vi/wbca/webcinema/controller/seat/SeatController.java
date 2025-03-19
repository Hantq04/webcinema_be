package vi.wbca.webcinema.controller.seat;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.dto.SeatDTO;
import vi.wbca.webcinema.service.seatService.SeatService;
import vi.wbca.webcinema.util.Constants;
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
    @PreAuthorize("hasRole('" + Constants.USER + "') or hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> insertSeat(@Valid @RequestBody SeatDTO request) {

        logger.info("----------Web Cinema: Insert New Seat----------");

        SeatDTO responseData = seatService.insertSeat(request);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Insert seat successfully.", responseData)
        );
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('" + Constants.USER + "') or hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> updateSeat(@Valid @RequestBody SeatDTO request) {

        logger.info("----------Web Cinema: Update Seat----------");

        seatService.updateSeat(request);

        Map<String, String> responseData = new HashMap<>();
        responseData.put(Constants.LINE, request.getLine());
        responseData.put(Constants.NUMBER, request.getNumber().toString());

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Updated seat successfully.", responseData)
        );
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> deleteSeat(@Valid @RequestParam Long id) {

        logger.info("----------Web Cinema: Delete Seat----------");

        seatService.deleteSeat(id);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Deleted seat successfully.", "")
        );
    }

    @PutMapping("/refresh")
    @PreAuthorize("hasRole('" + Constants.USER + "') or hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> refreshSeat(@RequestParam String code) {

        logger.info("----------Web Cinema: Update Seat Status----------");

        seatService.refreshSeat(code);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Updated seat status successfully.", "")
        );
    }
}
