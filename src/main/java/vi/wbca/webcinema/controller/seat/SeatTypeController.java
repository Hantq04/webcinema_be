package vi.wbca.webcinema.controller.seat;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.model.SeatType;
import vi.wbca.webcinema.service.seatTypeService.SeatTypeService;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/seat-type")
public class SeatTypeController {
    private static final Logger logger = Logger.getLogger(SeatTypeController.class.getName());
    private final SeatTypeService seatTypeService;

    @PostMapping("/insert")
    public ResponseEntity<ResponseObject> insertSeatType(@Valid @RequestBody SeatType seatType) {

        logger.info("----------Web Cinema: Insert New Seat Type----------");

        SeatType response = seatTypeService.insertSeatType(seatType);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Insert seat type successfully.", response)
        );
    }

    @GetMapping("/get-all-type")
    public List<SeatType> getAllType() {
        return seatTypeService.getAllType();
    }
}
