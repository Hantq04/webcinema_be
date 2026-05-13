package vi.wbca.webcinema.controller.movie;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.model.request.BookingRequest;
import vi.wbca.webcinema.model.request.TicketCancelRequest;
import vi.wbca.webcinema.model.response.BookingResponse;
import vi.wbca.webcinema.model.response.TicketResponse;
import vi.wbca.webcinema.service.TicketService;
import vi.wbca.webcinema.util.Constants;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ticket")
public class TicketController {
    private static final Logger logger = Logger.getLogger(TicketController.class.getName());
    private final TicketService ticketService;
    private final MessageSource messageSource;

    @PostMapping("/create")
    @PreAuthorize(Constants.PERM_USER_STAFF_ADMIN)
    @Operation(summary = "Tạo vé mới")
    public ResponseEntity<ResponseObject> insertTicket(@Valid @RequestBody BookingRequest request) {
        logger.info("----------Web Cinema: Insert New Ticket----------");
        BookingResponse responseData = ticketService.insertTicket(request);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.insert_ticket", null, locale);
        return ResponseEntity.status(HttpStatus.CREATED).body(
            new ResponseObject(HttpStatus.CREATED, message, responseData)
        );
    }

    @PostMapping("/cancel")
    @PreAuthorize(Constants.PERM_USER_STAFF_ADMIN)
    @Operation(summary = "Hủy ticket hold hoặc booking đang chờ thanh toán")
    public ResponseEntity<ResponseObject> cancelTicket(@Valid @RequestBody TicketCancelRequest request) {
        logger.info("----------Web Cinema: Cancel Ticket Hold----------");
        ticketService.cancelTicket(request);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.delete", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, null)
        );
    }

    @DeleteMapping("/delete")
    @PreAuthorize(Constants.PERM_ADMIN_ONLY)
    @Operation(summary = "Xóa vé theo mã")
    public ResponseEntity<ResponseObject> deleteTicket(@Valid @RequestParam String code) {
        logger.info("----------Web Cinema: Delete Ticket----------");
        ticketService.deleteTicket(code);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.delete", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, null)
        );
    }

    @GetMapping("/get-all-ticket")
    @PreAuthorize(Constants.PERM_STAFF_ADMIN)
    @Operation(summary = "Lấy tất cả vé")
    public ResponseEntity<ResponseObject> getAllTicket() {
        logger.info("----------Web Cinema: Get All Ticket----------");
        List<TicketResponse> responseData = ticketService.getAllTicket();
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.get_all_ticket", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }
}
