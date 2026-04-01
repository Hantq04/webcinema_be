package vi.wbca.webcinema.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.model.entity.user.UserStatus;
import vi.wbca.webcinema.service.UserStatusService;
import vi.wbca.webcinema.util.Constants;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user/status")
public class UserStatusController {
    private static final Logger logger = Logger.getLogger(UserStatusController.class.getName());
    private final UserStatusService userStatusService;
    private final MessageSource messageSource;

    @PostMapping("/insert")
    @PreAuthorize("hasRole('" + Constants.USER + "') or hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> insertUserStatus(@RequestBody UserStatus userStatus) {
        logger.info("----------Web Cinema: Insert New User Status----------");
        UserStatus responseData = userStatusService.insertUserStatus(userStatus);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.insert_user_status", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }

    @GetMapping("/get-all-status")
    @PreAuthorize("hasRole('" + Constants.USER + "') or hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> getAllStatus() {
        logger.info("----------Web Cinema: Get All User Status----------");
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.get_all_user_status", null, locale);
        List<UserStatus> responseData = userStatusService.getAllStatus();
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }
}
