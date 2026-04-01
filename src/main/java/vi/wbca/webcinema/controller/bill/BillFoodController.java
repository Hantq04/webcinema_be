package vi.wbca.webcinema.controller.bill;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.model.dto.bill.BillFoodDTO;
import vi.wbca.webcinema.model.entity.bill.Bill;
import vi.wbca.webcinema.service.BillFoodService;
import vi.wbca.webcinema.util.Constants;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.Locale;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bill/food")
public class BillFoodController {
    private static final Logger logger = Logger.getLogger(BillFoodController.class.getName());
    private final BillFoodService billFoodService;
    private final MessageSource messageSource;

    @PostMapping("/insert")
    @PreAuthorize(Constants.PERM_STAFF_ADMIN)
    public ResponseEntity<ResponseObject> insertBillFood(@Valid @RequestBody BillFoodDTO request, Bill bill) {
        logger.info("----------Web Cinema: Insert New Bill Food----------");
        billFoodService.insertBillFood(request, bill);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.insert_bill_food", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, null)
        );
    }

    @DeleteMapping("/delete")
    @PreAuthorize(Constants.PERM_ADMIN_ONLY)
    public ResponseEntity<ResponseObject> deleteBillFood(@Valid @RequestParam Long id) {
        logger.info("----------Web Cinema: Delete Bill Food----------");
        billFoodService.deleteFood(id);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.delete", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, "")
        );
    }
}
