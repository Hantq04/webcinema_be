package vi.wbca.webcinema.controller.seat;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.model.seat.SeatStatus;
import vi.wbca.webcinema.service.SeatStatusService;
import vi.wbca.webcinema.util.Constants;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/seat/status")
public class SeatStatusController {
    private static final Logger logger = Logger.getLogger(SeatStatusController.class.getName());
    private final SeatStatusService seatStatusService;

    @PostMapping("/insert")
    @PreAuthorize("hasRole('" + Constants.USER + "') or hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> insertSeatStatus(@Valid @RequestBody SeatStatus seatStatus) {
        logger.info("----------Web Cinema: Insert New Seat Status----------");
        SeatStatus response = seatStatusService.insertSeatStatus(seatStatus);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Insert seat status successfully.", response)
        );
    }

    @GetMapping("/get-all-status")
    @PreAuthorize("hasRole('" + Constants.USER + "') or hasRole('" + Constants.ADMIN + "')")
    public List<SeatStatus> getAllStatus() {
        logger.info("----------Web Cinema: Get All Seat Status----------");
        return seatStatusService.getAllStatus();
    }
}
