package vi.wbca.webcinema.controller.movie;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.model.dto.schedule.ScheduleDTO;
import vi.wbca.webcinema.model.request.ScheduleMovieFilterRequest;
import vi.wbca.webcinema.model.response.ScheduleResponse;
import vi.wbca.webcinema.model.response.ScheduleGroupByDateResponse;
import vi.wbca.webcinema.validation.groupValidate.schedule.InsertSchedule;
import vi.wbca.webcinema.validation.groupValidate.schedule.UpdateSchedule;
import vi.wbca.webcinema.service.ScheduleService;
import vi.wbca.webcinema.util.Constants;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/schedule")
public class ScheduleController {
    private static final Logger logger = Logger.getLogger(ScheduleController.class.getName());
    private final ScheduleService scheduleService;
    private final MessageSource messageSource;

    @PostMapping("/save")
    @PreAuthorize(Constants.PERM_STAFF_ADMIN)
    @Operation(summary = "Thêm lịch chiếu mới")
    public ResponseEntity<ResponseObject> insertSchedule(@Validated(InsertSchedule.class) @RequestBody ScheduleDTO request) {
        logger.info("----------Web Cinema: Insert New Schedule----------");
        ScheduleDTO responseData = scheduleService.insertSchedule(request);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.insert_schedule", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }

    @PutMapping("/update")
    @PreAuthorize(Constants.PERM_STAFF_ADMIN)
    @Operation(summary = "Cập nhật lịch chiếu")
    public ResponseEntity<ResponseObject> updateSchedule(@Validated(UpdateSchedule.class) @RequestBody ScheduleDTO request) {
        logger.info("----------Web Cinema: Update Schedule----------");
        scheduleService.updateSchedule(request);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.update", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, null)
        );
    }

    @DeleteMapping("/delete")
    @PreAuthorize(Constants.PERM_ADMIN_ONLY)
    @Operation(summary = "Xóa lịch chiếu theo mã và ID phim")
    public ResponseEntity<ResponseObject> deleteSchedule(@Valid @RequestParam String code, Long movieId) {
        logger.info("----------Web Cinema: Delete Schedule----------");
        scheduleService.deleteSchedule(code, movieId);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.delete", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, "")
        );
    }

    @PutMapping("/deactivate-expired")
    @PreAuthorize(Constants.PERM_STAFF_ADMIN)
    @Operation(summary = "Vô hiệu hóa các lịch chiếu đã hết hạn")
    public ResponseEntity<ResponseObject> deactivateExpiredSchedule() {
        logger.info("----------Web Cinema: Deactivate Expired Schedule----------");
        scheduleService.deactivateExpiredSchedule();
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.deactivate_expired_schedule", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, "")
        );
    }

    @GetMapping("/get-all-schedule")
    @PreAuthorize(Constants.PERM_STAFF_ADMIN)
    @Operation(summary = "Lấy tất cả lich chiếu")
    public ResponseEntity<ResponseObject> getAllTicket() {
        logger.info("----------Web Cinema: Get All Schedule----------");
        List<ScheduleResponse> responseData = scheduleService.getAllSchedule();
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.get_all_schedule", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }

    @GetMapping("/movie")
    @Operation(summary = "Lấy lịch chiếu theo phim")
    public ResponseEntity<ResponseObject> getSchedulesByMovieGroupedByDate(@Valid @ModelAttribute ScheduleMovieFilterRequest request) {
        logger.info("----------Web Cinema: Get Schedules For Movie----------");
        List<ScheduleGroupByDateResponse> responseData = scheduleService.getSchedulesByMovieGroupedByDate(request);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.get_schedule_grouped", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }
}
