package vi.wbca.webcinema.controller.user;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.model.dto.token.TokenDTO;
import vi.wbca.webcinema.model.dto.user.UserDTO;
import vi.wbca.webcinema.model.request.LoginRequest;
import vi.wbca.webcinema.validation.groupValidate.user.DeleteUser;
import vi.wbca.webcinema.validation.groupValidate.user.InsertUser;
import vi.wbca.webcinema.validation.groupValidate.user.UpdateUser;
import vi.wbca.webcinema.model.response.LoginResponse;
import vi.wbca.webcinema.model.response.UserResponse;
import vi.wbca.webcinema.service.AccountService;
import vi.wbca.webcinema.service.RefreshTokenService;
import vi.wbca.webcinema.service.UserService;
import vi.wbca.webcinema.util.Constants;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    private static final Logger logger = Logger.getLogger(UserController.class.getName());
    private final UserService userService;
    private final AccountService accountService;
    private final RefreshTokenService refreshTokenService;
    private final MessageSource messageSource;

    @PostMapping("/register")
    public ResponseEntity<ResponseObject> register(@Validated(InsertUser.class) @RequestBody UserDTO request) {
        logger.info("----------Web Cinema: Register New User----------");
        userService.register(request);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.register", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, null)
        );
    }

    @PostMapping("/staff-register")
    public ResponseEntity<ResponseObject> staffRegister(@Validated(InsertUser.class) @RequestBody UserDTO request) {
        logger.info("----------Web Cinema: Register New Staff Account----------");
        userService.staffRegister(request);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.staff_register", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, null)
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseObject> login(@Valid @RequestBody LoginRequest request) {
        logger.info("----------Web Cinema: Login Page----------");
        LoginResponse responseData = userService.login(request);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.login", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }

    @GetMapping("/verify-email")
    public ResponseEntity<ResponseObject> verifyEmail(@Valid @RequestParam("token") String token) {
        logger.info("----------Web Cinema: Verify Email----------");
        String result = accountService.validateToken(token);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.verify_email", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, result)
        );
    }

    @GetMapping("/resend-verify-email")
    public ResponseEntity<ResponseObject> resendVerifyEmail(@Valid @RequestParam String email) throws MessagingException, UnsupportedEncodingException {
        logger.info("----------Web Cinema: Resend Verify Email----------");
        String result = accountService.resendVerificationEmail(email);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.resend_verify_email", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, result)
        );
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> updateUser(@Validated(UpdateUser.class) @RequestBody UserDTO request) {
        logger.info("----------Web Cinema: Update User----------");
        userService.updateUser(request);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.update_user", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, null)
        );
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> deleteUser(@Validated(DeleteUser.class) @RequestParam List<String> userName) {
        logger.info("----------Web Cinema: Delete User----------");
        userService.deleteUser(userName);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.delete_user", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, "")
        );
    }

    @GetMapping("/forgot-password")
    public ResponseEntity<ResponseObject> forgotPassword(@Valid @RequestParam String email) throws MessagingException, UnsupportedEncodingException {
        logger.info("----------Web Cinema: Forgot Password----------");
        String result = accountService.sendChangePassword(email);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.forgot_password", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, result)
        );
    }

    @PutMapping("/change-password")
    public ResponseEntity<ResponseObject> changePassword(@Valid @RequestParam String token, String newPassword, String confirmPassword) {
        logger.info("----------Web Cinema: Change Password----------");
        String responseData = accountService.changePassword(token, newPassword, confirmPassword);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.change_password", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ResponseObject> refreshToken(@Valid @RequestParam String refreshToken) {
        logger.info("----------Web Cinema: Refresh Token----------");
        TokenDTO responseData = refreshTokenService.refreshToken(refreshToken);
        Locale locale = LocaleContextHolder.getLocale();
        String message = responseData.isNewToken() 
                ? messageSource.getMessage("success.refresh_token", null, locale)
                : messageSource.getMessage("success.token_still_valid", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }

    @GetMapping("/get-all")
    @PreAuthorize("hasRole('" + Constants.ADMIN + "')")
    public List<UserResponse> getAllUser() {
        logger.info("----------Web Cinema: List User----------");
        return userService.getAllUser();
    }

    @GetMapping("/find-by-id")
    @PreAuthorize("hasRole('" + Constants.USER + "') or hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> findById(@Valid @RequestParam Long id) {
        logger.info("----------Web Cinema: Get User----------");
        UserDTO responseData = userService.findById(id);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.find_user", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }
}
