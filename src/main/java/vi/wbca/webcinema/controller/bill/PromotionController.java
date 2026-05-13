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
import vi.wbca.webcinema.model.dto.ticket.PromotionDTO;
import vi.wbca.webcinema.model.response.PromotionResponse;
import vi.wbca.webcinema.service.PromotionService;
import vi.wbca.webcinema.util.Constants;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/promotion")
public class PromotionController {
    private static final Logger logger = Logger.getLogger(PromotionController.class.getName());
    private final PromotionService promotionService;
    private final MessageSource messageSource;

    @PostMapping("/save")
    @PreAuthorize(Constants.PERM_STAFF_ADMIN)
    @Operation(summary = "Thêm khuyến mãi mới")
    public ResponseEntity<ResponseObject> insertPromotion(@Valid @RequestBody PromotionDTO request) {
        logger.info("----------Web Cinema: Insert New Promotion----------");
        promotionService.insertPromotion(request);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.insert_promotion", null, locale);
        return ResponseEntity.status(HttpStatus.CREATED).body(
            new ResponseObject(HttpStatus.CREATED, message, null)
        );
    }

    @DeleteMapping("/delete")
    @PreAuthorize(Constants.PERM_ADMIN_ONLY)
    @Operation(summary = "Xóa khuyến mãi theo tên")
    public ResponseEntity<ResponseObject> deletePromotion(@Valid @RequestParam String name) {
        logger.info("----------Web Cinema: Delete Promotion----------");
        promotionService.deletePromotion(name);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.delete", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, null)
        );
    }

    @GetMapping("/get-all-promotion")
    @PreAuthorize(Constants.PERM_STAFF_ADMIN)
    @Operation(summary = "Lấy tất cả khuyến mãi")
    public ResponseEntity<ResponseObject> getAllPromotion() {
        logger.info("----------Web Cinema: Get All Promotion----------");
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.get_all_promotion", null, locale);
        List<PromotionResponse> responseData = promotionService.getAllPromotion();
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }
}
