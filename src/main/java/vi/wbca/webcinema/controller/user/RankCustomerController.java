package vi.wbca.webcinema.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.model.entity.user.RankCustomer;
import vi.wbca.webcinema.service.RankCustomerService;
import vi.wbca.webcinema.util.Constants;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rank")
public class RankCustomerController {
    private static final Logger logger = Logger.getLogger(RankCustomerController.class.getName());
    private final RankCustomerService rankCustomerService;
    private final MessageSource messageSource;

    @PostMapping("/insert")
    @PreAuthorize("hasRole('" + Constants.USER + "') or hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> insertRank(@RequestBody RankCustomer rankCustomer) {
        logger.info("----------Web Cinema: Insert New Rank Customer----------");
        rankCustomerService.insertRank(rankCustomer);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.insert_rank", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, null)
        );
    }

    @GetMapping("/get-all-rank")
    @PreAuthorize("hasRole('" + Constants.USER + "') or hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> getAllRank() {
        logger.info("----------Web Cinema: Get All Rank Customer----------");
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.get_all_rank", null, locale);
        List<RankCustomer> responseData = rankCustomerService.getAllRank();
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }
}
