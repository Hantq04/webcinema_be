package vi.wbca.webcinema.controller.setting;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import vi.wbca.webcinema.model.request.RevenueSummaryRequest;
import vi.wbca.webcinema.model.dto.revenue.RevenueTimePointDTO;
import vi.wbca.webcinema.service.RevenueService;
import vi.wbca.webcinema.util.Constants;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/revenue")
public class RevenueController {
    private static final Logger logger = Logger.getLogger(RevenueController.class.getName());
    private final RevenueService revenueService;
    private final MessageSource messageSource;

    @GetMapping("/summary")
    @PreAuthorize(Constants.PERM_ADMIN_ONLY)
    @Operation(summary = "Lấy thống kê doanh thu theo thời gian")
    public ResponseEntity<ResponseObject> getRevenueSummary(@Valid @ModelAttribute RevenueSummaryRequest request) {
        logger.info("----------Web Cinema: Revenue Summary----------");
        LocalDateTime fromTime = request.getFromDate().atStartOfDay();
        LocalDateTime toTime = request.getToDate().atTime(LocalTime.MAX);
        List<RevenueTimePointDTO> responseData = revenueService.getRevenueSummary(
            fromTime,
            toTime,
            request.getGroupBy(),
            request.getCinemaId(),
            request.getRoomId(),
            request.getMovieId()
        );
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.get_revenue_summary", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }

    @GetMapping("/summary/export")
    @PreAuthorize(Constants.PERM_ADMIN_ONLY)
    @Operation(summary = "Xuất Excel thống kê doanh thu")
    public ResponseEntity<byte[]> exportRevenueSummary(@Valid @ModelAttribute RevenueSummaryRequest request) {
        logger.info("----------Web Cinema: Export Revenue Summary----------");
        LocalDateTime fromTime = request.getFromDate().atStartOfDay();
        LocalDateTime toTime = request.getToDate().atTime(LocalTime.MAX);
        byte[] fileBytes = revenueService.exportRevenueSummary(
            fromTime,
            toTime,
            request.getGroupBy(),
            request.getCinemaId(),
            request.getRoomId(),
            request.getMovieId()
        );
        String fileName = String.format("revenue-summary-%s-%s.xlsx", request.getFromDate(), request.getToDate());
        return ResponseEntity.status(HttpStatus.OK)
            .contentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .header("Content-Disposition", ContentDisposition.attachment().filename(fileName).build().toString())
            .body(fileBytes);
    }
}
