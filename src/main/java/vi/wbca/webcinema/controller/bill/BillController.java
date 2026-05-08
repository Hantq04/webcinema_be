package vi.wbca.webcinema.controller.bill;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.model.dto.bill.BillDTO;
import vi.wbca.webcinema.model.response.BillHoldResponse;
import vi.wbca.webcinema.model.response.PrintTicketResponse;
import vi.wbca.webcinema.model.response.TransactionHistoryResponse;
import vi.wbca.webcinema.service.BillService;
import vi.wbca.webcinema.service.PrintTicketService;
import vi.wbca.webcinema.service.TransactionHistoryService;
import vi.wbca.webcinema.util.Constants;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.Locale;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bill")
public class BillController {
    private static final Logger logger = Logger.getLogger(BillController.class.getName());
    private final BillService billService;
    private final TransactionHistoryService transactionHistoryService;
    private final PrintTicketService printTicketService;
    private final MessageSource messageSource;

    @PostMapping("/create")
    @PreAuthorize(Constants.PERM_USER_STAFF_ADMIN)
    @Operation(summary = "Tạo hóa đơn mới")
    public ResponseEntity<ResponseObject> createBill(@Valid @RequestBody BillDTO request) {
        logger.info("----------Web Cinema: Insert New Bill----------");
        BillHoldResponse responseData = billService.createBill(request);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.insert_bill", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
            new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }

    @PutMapping("/update")
    @PreAuthorize(Constants.PERM_STAFF_ADMIN)
    @Operation(summary = "Cập nhật hóa đơn")
    public ResponseEntity<ResponseObject> updateBill(@Valid @RequestBody BillDTO request) {
        logger.info("----------Web Cinema: Update Bill----------");
        billService.updateBill(request);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.update", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, null)
        );
    }

    @DeleteMapping("/cancel")
    @PreAuthorize(Constants.PERM_USER_STAFF_ADMIN)
    @Operation(summary = "Hủy giữ chỗ hóa đơn")
    public ResponseEntity<ResponseObject> cancelBill(@Valid @RequestParam String tradingCode) {
        logger.info("----------Web Cinema: Cancel Bill Hold----------");
        billService.cancelBill(tradingCode);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.delete", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, "")
        );
    }

    @DeleteMapping("/delete")
    @PreAuthorize(Constants.PERM_ADMIN_ONLY)
    @Operation(summary = "Xóa hóa đơn theo mã giao dịch")
    public ResponseEntity<ResponseObject> deleteBill(@Valid @RequestParam String tradingCode) {
        logger.info("----------Web Cinema: Delete Bill----------");
        billService.deleteBill(tradingCode);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.delete", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, "")
        );
    }

    @GetMapping("/list")
    @PreAuthorize(Constants.PERM_USER_STAFF_ADMIN)
    @Operation(summary = "Lấy danh sách hóa đơn")
    public ResponseEntity<ResponseObject> getBillList(@RequestParam(required = false) Long cinemaId,
                                                      @RequestParam int page,
                                                      @RequestParam int size) {
        logger.info("----------Web Cinema: Get Bill List----------");
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<TransactionHistoryResponse> responseData = transactionHistoryService.getTransactionHistory(cinemaId, pageable);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.get_bill_list", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }

    @GetMapping("/detail")
    @PreAuthorize(Constants.PERM_USER_STAFF_ADMIN)
    @Operation(summary = "Lấy chi tiết hóa đơn")
    public ResponseEntity<ResponseObject> getBillDetail(@RequestParam String tradingCode) {
        logger.info("----------Web Cinema: Get Bill Detail: " + tradingCode + "----------");
        PrintTicketResponse responseData = printTicketService.getPrintTicketData(tradingCode);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.get_bill_detail", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }
}
