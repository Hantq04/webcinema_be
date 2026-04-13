package vi.wbca.webcinema.controller.user;

import io.swagger.v3.oas.annotations.Operation;
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
import vi.wbca.webcinema.model.request.ChangePasswordRequest;
import vi.wbca.webcinema.model.request.ResetPasswordRequest;
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
    @Operation(summary = "Đăng ký tài khoản người dùng mới")
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
    @Operation(summary = "Đăng ký tài khoản nhân viên mới")
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
    @Operation(summary = "Đăng nhập tài khoản người dùng")
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
    @Operation(summary = "Xác minh email người dùng")
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
    @Operation(summary = "Gửi lại liên kết xác minh email")
    public ResponseEntity<ResponseObject> resendVerifyEmail(@Valid @RequestParam String email) throws MessagingException, UnsupportedEncodingException {
        logger.info("----------Web Cinema: Resend Verify Email----------");
        String result = accountService.resendVerificationEmail(email);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.resend_verify_email", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, result)
        );
    }

    @PutMapping("/update-profile")
    @PreAuthorize(Constants.PERM_USER_STAFF_ADMIN)
    @Operation(summary = "Cập nhật thông tin người dùng")
    public ResponseEntity<ResponseObject> updateUser(@Validated(UpdateUser.class) @RequestBody UserDTO request) {
        logger.info("----------Web Cinema: Update User----------");
        userService.updateUser(request);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.update", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, null)
        );
    }

    @DeleteMapping("/delete")
    @PreAuthorize(Constants.PERM_ADMIN_ONLY)
    @Operation(summary = "Xóa tài khoản người dùng")
    public ResponseEntity<ResponseObject> deleteUser(@Validated(DeleteUser.class) @RequestParam List<String> userName) {
        logger.info("----------Web Cinema: Delete User----------");
        userService.deleteUser(userName);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.delete", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, "")
        );
    }

    @GetMapping("/forgot-password")
    @Operation(summary = "Gửi email đặt lại mật khẩu (Quên mật khẩu)")
    public ResponseEntity<ResponseObject> forgotPassword(@Valid @RequestParam String email) throws MessagingException, UnsupportedEncodingException {
        logger.info("----------Web Cinema: Forgot Password - Request Token----------");
        String result = accountService.sendChangePassword(email);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.forgot_password", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, result)
        );
    }

    @PutMapping("/change-password")
    @PreAuthorize(Constants.PERM_USER_STAFF_ADMIN)
    @Operation(summary = "Đổi mật khẩu (Người dùng đã đăng nhập)")
    public ResponseEntity<ResponseObject> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        logger.info("----------Web Cinema: Change Password - Logged In User----------");
        String responseData = accountService.changePasswordForLoggedInUser(
                request.getOldPassword(),
                request.getNewPassword(),
                request.getConfirmPassword()
        );
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.change_password", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }

    @PutMapping("/reset-password")
    @Operation(summary = "Đặt lại mật khẩu (OTP)")
    public ResponseEntity<ResponseObject> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        logger.info("----------Web Cinema: Reset Password - With Token----------");
        String responseData = accountService.resetPasswordWithToken(
                request.getToken(),
                request.getNewPassword(),
                request.getConfirmPassword()
        );
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, null, responseData)
        );
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Làm mới token xác thực")
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
    @PreAuthorize(Constants.PERM_STAFF_ADMIN)
    @Operation(summary = "Lấy danh sách tất cả người dùng")
    public ResponseEntity<ResponseObject> getAllUser() {
        logger.info("----------Web Cinema: Get All User----------");
        List<UserResponse> responseData = userService.getAllUser();
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("success.get_all_user", null, locale);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }

    @GetMapping("/find-by-id")
    @PreAuthorize(Constants.PERM_STAFF_ADMIN)
    @Operation(summary = "Tìm người dùng theo ID")
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
