package vi.wbca.webcinema.controller.bill;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.service.BillTicketService;
import vi.wbca.webcinema.util.Constants;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.Locale;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bill/ticket")
public class BillTicketController {
    private static final Logger logger = Logger.getLogger(BillTicketController.class.getName());
    private final BillTicketService billTicketService;
    private final MessageSource messageSource;

//    @PostMapping("/insert")
//    @PreAuthorize("hasRole('" + Constants.USER + "') or hasRole('" + Constants.ADMIN + "')")
//    public ResponseEntity<ResponseObject> insertBillTicket(@Valid @RequestBody BillTicketRequest request, Bill bill) {
//        logger.info("----------Web Cinema: Insert New Bill Ticket----------");
//        billTicketService.insertBillTicket(request, bill);
//
//        return ResponseEntity.status(HttpStatus.OK).body(
//                new ResponseObject(HttpStatus.OK, "Insert bill ticket successfully.", null)
//        );
//    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> deleteBillTicket(@Valid @RequestParam Long id) {
        logger.info("----------Web Cinema: Delete Bill Ticket----------");
        billTicketService.deleteTicket(id);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.delete", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, "")
        );
    }
}
