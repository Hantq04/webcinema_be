package vi.wbca.webcinema.controller.setting;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.model.entity.setting.Banner;
import vi.wbca.webcinema.model.request.BannerRequest;
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

    @PostMapping("/insert")
    @PreAuthorize(Constants.PERM_STAFF_ADMIN)
    public ResponseEntity<ResponseObject> insertBanner(@Valid @ModelAttribute BannerRequest request) throws IOException {
        bannerService.insertBanner(request);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.insert_banner", null, locale);
        return ResponseEntity.ok(
                new ResponseObject(HttpStatus.OK, message, null)
        );
    }

    @DeleteMapping("/delete")
    @PreAuthorize(Constants.PERM_ADMIN_ONLY)
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
    public ResponseEntity<ResponseObject> getAllBanner() {
        logger.info("----------Web Cinema: Get All Banner----------");
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.get_all_banner", null, locale);
        List<Banner> responseData = bannerService.getAllBanner();
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }
}
