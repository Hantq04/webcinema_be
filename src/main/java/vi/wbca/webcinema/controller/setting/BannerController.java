package vi.wbca.webcinema.controller.setting;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.model.request.BannerRequest;
import vi.wbca.webcinema.model.response.BannerResponse;
import vi.wbca.webcinema.service.BannerService;
import vi.wbca.webcinema.util.Constants;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/banner")
public class BannerController {
    private static final Logger logger = Logger.getLogger(BannerController.class.getName());
    private final BannerService bannerService;
    private final MessageSource messageSource;

    @PostMapping("/save")
    @PreAuthorize(Constants.PERM_STAFF_ADMIN)
    @Operation(summary = "Thêm banner mới")
    public ResponseEntity<ResponseObject> insertBanner(@Valid @ModelAttribute BannerRequest request) throws IOException {
        bannerService.insertBanner(request);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.insert_banner", null, locale);
        return ResponseEntity.status(HttpStatus.CREATED).body(
            new ResponseObject(HttpStatus.CREATED, message, null)
        );
    }

    @DeleteMapping("/delete")
    @PreAuthorize(Constants.PERM_ADMIN_ONLY)
    @Operation(summary = "Xóa banner theo ID")
    public ResponseEntity<ResponseObject> deleteBanner(@Valid @RequestParam Long id) {
        logger.info("----------Web Cinema: Delete Banner----------");
        bannerService.deleteBanner(id);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.delete", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, "")
        );
    }

    @GetMapping("/get-all-banner")
    @PreAuthorize(Constants.PERM_STAFF_ADMIN)
    @Operation(summary = "Lấy danh sách tất cả banner")
    public ResponseEntity<ResponseObject> getAllBanner() {
        logger.info("----------Web Cinema: Get All Banner----------");
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.get_all_banner", null, locale);
        List<BannerResponse> responseData = bannerService.getAllBanner();
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }
}
