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
import vi.wbca.webcinema.enums.VoucherStatusEnum;
import vi.wbca.webcinema.model.dto.ticket.UserPromotionDTO;
import vi.wbca.webcinema.model.request.SavePromotionRequest;
import vi.wbca.webcinema.model.request.ViewVoucherRequest;
import vi.wbca.webcinema.service.UserPromotionService;
import vi.wbca.webcinema.util.Constants;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user-promotion")
public class UserPromotionController {
    private static final Logger logger = Logger.getLogger(UserPromotionController.class.getName());
    private final UserPromotionService userPromotionService;
    private final MessageSource messageSource;

    @PostMapping("/save")
    @PreAuthorize(Constants.PERM_USER_STAFF_ADMIN)
    @Operation(summary = "Lưu khuyến mãi cho người dùng")
    public ResponseEntity<ResponseObject> savePromotion(@Valid @RequestBody SavePromotionRequest request) {
        logger.info("----------Web Cinema: Save Promotion for User----------");
        userPromotionService.savePromotion(request);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.insert_promotion", null, locale);
        return ResponseEntity.status(HttpStatus.CREATED).body(
            new ResponseObject(HttpStatus.CREATED, message, null)
        );
    }

    @GetMapping("/voucher-list")
    @PreAuthorize(Constants.PERM_USER_STAFF_ADMIN)
    @Operation(summary = "Lấy danh sách mã khuyến mãi")
    public ResponseEntity<ResponseObject> getUserPromotions(@Valid @ModelAttribute ViewVoucherRequest request) {
        logger.info("----------Web Cinema: Get User Promotions With Filters----------");
        List<UserPromotionDTO> result = userPromotionService.getUserPromotions(request);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.voucher-list", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, result)
        );
    }

    @GetMapping("/get-unused")
    @PreAuthorize(Constants.PERM_USER_STAFF_ADMIN)
    @Operation(hidden = true)
    public ResponseEntity<ResponseObject> getUnusedPromotions(@RequestParam Long userId) {
        ViewVoucherRequest request = ViewVoucherRequest.builder().userId(userId)
                .voucherStatus(VoucherStatusEnum.UNUSED).build();
        return getUserPromotions(request);
    }

    @GetMapping("/get-expired")
    @PreAuthorize(Constants.PERM_USER_STAFF_ADMIN)
    @Operation(hidden = true)
    public ResponseEntity<ResponseObject> getExpiredPromotions(@RequestParam Long userId) {
        ViewVoucherRequest request = ViewVoucherRequest.builder().userId(userId)
                .voucherStatus(VoucherStatusEnum.EXPIRED).build();
        return getUserPromotions(request);
    }
}
