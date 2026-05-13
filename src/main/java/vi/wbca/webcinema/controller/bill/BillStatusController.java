package vi.wbca.webcinema.controller.bill;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.model.entity.bill.BillStatus;
import vi.wbca.webcinema.service.BillStatusService;
import vi.wbca.webcinema.util.Constants;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bill/status")
public class BillStatusController {
    private static final Logger logger = Logger.getLogger(BillStatusController.class.getName());
    private final BillStatusService billStatusService;
    private final MessageSource messageSource;

    @PostMapping("/save")
    @PreAuthorize(Constants.PERM_STAFF_ADMIN)
    @Operation(summary = "Thêm trạng thái hóa đơn mới")
    public ResponseEntity<ResponseObject> insertBillStatus(@Valid @RequestBody BillStatus billStatus) {
        logger.info("----------Web Cinema: Insert New Bill Status----------");
        BillStatus responseData = billStatusService.insertBillStatus(billStatus);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.insert_bill_status", null, locale);
        return ResponseEntity.status(HttpStatus.CREATED).body(
            new ResponseObject(HttpStatus.CREATED, message, responseData)
        );
    }

    @GetMapping("/get-all-status")
    @PreAuthorize(Constants.PERM_STAFF_ADMIN)
    @Operation(summary = "Lấy tất cả trạng thái hóa đơn")
    public ResponseEntity<ResponseObject> getAllStatus() {
        logger.info("----------Web Cinema: Get All Bill Status----------");
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.get_all_bill_status", null, locale);
        List<BillStatus> responseData = billStatusService.getAllStatus();
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }
}
