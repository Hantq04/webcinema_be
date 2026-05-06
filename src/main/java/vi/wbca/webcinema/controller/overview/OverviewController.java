package vi.wbca.webcinema.controller.overview;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vi.wbca.webcinema.service.OverviewService;
import vi.wbca.webcinema.util.Constants;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.Locale;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/overview")
public class OverviewController {
    private static final Logger logger = Logger.getLogger(OverviewController.class.getName());
    private final OverviewService overviewService;
    private final MessageSource messageSource;

    @GetMapping
    @PreAuthorize(Constants.PERM_STAFF_ADMIN)
    @Operation(summary = "Lấy dữ liệu tổng quan")
    public ResponseEntity<ResponseObject> getOverview() {
        logger.info("----------Web Cinema: Get Overview----------");
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.get_overview", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, overviewService.getOverview())
        );
    }
}