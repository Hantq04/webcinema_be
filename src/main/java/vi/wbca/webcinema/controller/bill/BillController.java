package vi.wbca.webcinema.controller.bill;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.model.dto.bill.BillDTO;
import vi.wbca.webcinema.model.dto.bill.BillTicketDTO;
import vi.wbca.webcinema.service.BillService;
import vi.wbca.webcinema.util.Constants;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bill")
public class BillController {
    private static final Logger logger = Logger.getLogger(BillController.class.getName());
    private final BillService billService;
    private final MessageSource messageSource;

    @PostMapping("/insert")
    @PreAuthorize("hasRole('" + Constants.USER + "') or hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> createBill(@Valid @RequestBody BillDTO request) {
        logger.info("----------Web Cinema: Insert New Bill----------");
        billService.createBill(request);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.insert_bill", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, null)
        );
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('" + Constants.USER + "') or hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> updateBill(@Valid @RequestBody BillDTO request) {
        logger.info("----------Web Cinema: Update Bill----------");
        billService.updateBill(request);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.update", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, null)
        );
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> deleteBill(@Valid @RequestParam String tradingCode) {
        logger.info("----------Web Cinema: Delete Bill----------");
        billService.deleteBill(tradingCode);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.delete", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, "")
        );
    }
}
