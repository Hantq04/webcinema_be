package vi.wbca.webcinema.controller.movie;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.dto.ScheduleDTO;
import vi.wbca.webcinema.groupValidate.schedule.InsertSchedule;
import vi.wbca.webcinema.groupValidate.schedule.UpdateSchedule;
import vi.wbca.webcinema.service.scheduleService.ScheduleService;
import vi.wbca.webcinema.util.Constants;
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
    public ResponseEntity<ResponseObject> insertSchedule(@Validated(InsertSchedule.class) @RequestBody ScheduleDTO request) {

        logger.info("----------Web Cinema: Insert New Schedule----------");

        ScheduleDTO responseData = scheduleService.insertSchedule(request);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Insert schedule successfully.", responseData)
        );
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseObject> updateSchedule(@Validated(UpdateSchedule.class) @RequestBody ScheduleDTO request) {

        logger.info("----------Web Cinema: Update Schedule----------");

        scheduleService.updateSchedule(request);

        Map<String, String> responseData = new HashMap<>();
        responseData.put(Constants.PRICE, request.getPrice().toString());
        responseData.put(Constants.START_TIME, request.getStartAt().toString());
        responseData.put(Constants.END_TIME, request.getEndAt().toString());
        responseData.put(Constants.CODE, request.getCode());
        responseData.put(Constants.NAME, request.getName());

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Updated schedule successfully.", responseData)
        );
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ResponseObject> deleteSchedule(@Valid @RequestParam String code, Long movieId) {

        logger.info("----------Web Cinema: Delete Schedule----------");

        scheduleService.deleteSchedule(code, movieId);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Deleted schedule successfully.", "")
        );
    }
}
