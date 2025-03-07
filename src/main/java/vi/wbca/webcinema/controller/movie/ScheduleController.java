package vi.wbca.webcinema.controller.movie;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.dto.ScheduleDTO;
import vi.wbca.webcinema.service.scheduleService.ScheduleService;
import vi.wbca.webcinema.util.Informations;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/schedule")
public class ScheduleController {
    private static final Logger logger = Logger.getLogger(ScheduleController.class.getName());
    private final ScheduleService scheduleService;

    @PostMapping("/insert")
    public ResponseEntity<ResponseObject> insertSchedule(@Valid @RequestBody ScheduleDTO request) {

        logger.info("----------Web Cinema: Insert New Schedule----------");

        ScheduleDTO responseData = scheduleService.insertSchedule(request);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Insert schedule successfully.", responseData)
        );
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseObject> updateSchedule(@Valid @RequestBody ScheduleDTO request) {

        logger.info("----------Web Cinema: Update Schedule----------");

        scheduleService.updateSchedule(request);

        Map<String, String> responseData = new HashMap<>();
        responseData.put(Informations.PRICE, request.getPrice().toString());
        responseData.put(Informations.START_TIME, request.getStartAt().toString());
        responseData.put(Informations.END_TIME, request.getEndAt().toString());
        responseData.put(Informations.CODE, request.getCode());
        responseData.put(Informations.NAME, request.getName());
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Updated schedule successfully.", responseData)
        );
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ResponseObject> deleteSchedule(@Valid @RequestBody String name, Long movieId) {

        logger.info("----------Web Cinema: Delete Schedule----------");

        scheduleService.deleteSchedule(name, movieId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Deleted schedule successfully.", "")
        );
    }
}
