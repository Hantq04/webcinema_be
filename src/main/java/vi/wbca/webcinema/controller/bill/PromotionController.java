package vi.wbca.webcinema.controller.bill;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.model.dto.ticket.PromotionDTO;
import vi.wbca.webcinema.model.entity.bill.Promotion;
import vi.wbca.webcinema.service.PromotionService;
import vi.wbca.webcinema.util.Constants;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/promotion")
public class PromotionController {
    private static final Logger logger = Logger.getLogger(PromotionController.class.getName());
    private final PromotionService promotionService;
    private final MessageSource messageSource;

    @PostMapping("/insert")
    @PreAuthorize("hasRole('" + Constants.USER + "') or hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> insertPromotion(@Valid @RequestBody PromotionDTO request) {
        logger.info("----------Web Cinema: Insert New Promotion----------");
        promotionService.insertPromotion(request);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.insert_promotion", null, locale);
        Map<String, String> responseData = new HashMap<>();
        responseData.put(Constants.PERCENT, request.getPercent().toString());
        responseData.put(Constants.QUANTITY, request.getQuantity().toString());
        responseData.put(Constants.TYPE, request.getPromotionType().toString());
        responseData.put(Constants.DESCRIPTION, request.getDescription());
        responseData.put(Constants.NAME, request.getName());
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> deletePromotion(@Valid @RequestParam String name) {
        logger.info("----------Web Cinema: Delete Promotion----------");
        promotionService.deletePromotion(name);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.delete", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, "")
        );
    }

    @GetMapping("/get-all-promotion")
    @PreAuthorize("hasRole('" + Constants.USER + "') or hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> getAllPromotion() {
        logger.info("----------Web Cinema: Get All Promotion----------");
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.get_all_promotion", null, locale);
        List<Promotion> responseData = promotionService.getAllPromotion();
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }
}
