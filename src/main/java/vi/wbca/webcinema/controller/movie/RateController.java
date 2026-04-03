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
import vi.wbca.webcinema.model.entity.movie.Rate;
import vi.wbca.webcinema.service.RateService;
import vi.wbca.webcinema.util.Constants;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rate")
public class RateController {
    private static final Logger logger = Logger.getLogger(RateController.class.getName());
    private final RateService rateService;
    private final MessageSource messageSource;

    @PostMapping("/save")
    @PreAuthorize(Constants.PERM_STAFF_ADMIN)
    @Operation(summary = "Thêm mức giá mới")
    public ResponseEntity<ResponseObject> insertRate(@Valid @RequestBody Rate rate) {
        logger.info("----------Web Cinema: Insert New Rate----------");
        Rate responseData = rateService.insertRate(rate);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.insert_rate", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }

    @GetMapping("/get-all-rate")
    @PreAuthorize(Constants.PERM_STAFF_ADMIN)
    @Operation(summary = "Lấy tất cả mức giá")
    public ResponseEntity<ResponseObject> getAllRate() {
        logger.info("----------Web Cinema: Get All Rate----------");
        List<Rate> responseData = rateService.getAllRate();
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.get_all_rate", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }
}
