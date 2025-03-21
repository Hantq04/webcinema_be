package vi.wbca.webcinema.controller.user;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.dto.token.TokenDTO;
import vi.wbca.webcinema.dto.user.UserDTO;
import vi.wbca.webcinema.groupValidate.user.DeleteUser;
import vi.wbca.webcinema.groupValidate.user.InsertUser;
import vi.wbca.webcinema.groupValidate.user.LoginUser;
import vi.wbca.webcinema.groupValidate.user.UpdateUser;
import vi.wbca.webcinema.service.accountService.AccountService;
import vi.wbca.webcinema.service.refreshTokenService.RefreshTokenService;
import vi.wbca.webcinema.service.userService.UserService;
import vi.wbca.webcinema.util.Constants;
import vi.wbca.webcinema.util.response.ResponseObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    private static final Logger logger = Logger.getLogger(UserController.class.getName());
    private final UserService userService;
    private final AccountService accountService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<ResponseObject> register(@Validated(InsertUser.class) @RequestBody UserDTO request) {

        logger.info("----------Web Cinema: Register New User----------");

        userService.register(request);

        Map<String, String> responseData = new HashMap<>();
        responseData.put(Constants.USER_NAME, request.getUserName());
        responseData.put(Constants.EMAIL, request.getEmail());
        responseData.put(Constants.LIST_ROLE, request.getListRoles().toString());

        String message = "User registered successfully. Please check your email to get your OTP for verification.";

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseObject> login(@Validated(LoginUser.class) @RequestBody UserDTO userDTO) {

        logger.info("----------Web Cinema: Login Page----------");

        UserDTO responseData = userService.login(userDTO);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "User login successfully.", responseData)
        );
    }

    @GetMapping("/verify-email")
    public ResponseEntity<ResponseObject> verifyEmail(@Valid @RequestParam("token") String token) {

        logger.info("----------Web Cinema: Verify Email----------");

        String result = accountService.validateToken(token);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "User verified successfully.", result)
        );
    }

    @GetMapping("/resend-verify-email")
    public ResponseEntity<ResponseObject> resendVerifyEmail(@Valid @RequestParam String email) throws MessagingException, UnsupportedEncodingException {

        logger.info("----------Web Cinema: Resend Verify Email----------");

        String result = accountService.resendVerificationEmail(email);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Your OTP has been resent.", result)
        );
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('" + Constants.USER + "') or hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> updateUser(@Validated(UpdateUser.class) @RequestBody UserDTO request) {

        logger.info("----------Web Cinema: Update User----------");

        userService.updateUser(request);

        Map<String, String> responseData = new HashMap<>();
        responseData.put(Constants.USER_NAME, request.getUserName());
        responseData.put(Constants.EMAIL, request.getEmail());
        responseData.put(Constants.PHONE_NUMBER, request.getPhoneNumber());
        responseData.put(Constants.LIST_ROLE, request.getListRoles().toString());

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "User updated successfully.", responseData)
        );
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> deleteUser(@Validated(DeleteUser.class) @RequestParam List<String> userName) {

        logger.info("----------Web Cinema: Delete User----------");

        userService.deleteUser(userName);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "User deleted successfully.", "")
        );
    }

    @GetMapping("/forgot-password")
    public ResponseEntity<ResponseObject> forgotPassword(@Valid @RequestParam String email) throws MessagingException, UnsupportedEncodingException {

        logger.info("----------Web Cinema: Forgot Password----------");

        String result = accountService.sendChangePassword(email);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Your OTP has been sent.", result)
        );
    }

    @PutMapping("/change-password")
    public ResponseEntity<ResponseObject> changePassword(@Valid @RequestParam String token, String newPassword, String confirmPassword) {

        logger.info("----------Web Cinema: Change Password----------");

        String responseData = accountService.changePassword(token, newPassword, confirmPassword);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Change password successfully.", responseData)
        );
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ResponseObject> refreshToken(@Valid @RequestParam String refreshToken) {

        logger.info("----------Web Cinema: Refresh Token----------");

        TokenDTO responseData = refreshTokenService.refreshToken(refreshToken);
        String message = responseData.isNewToken()
                ? "Refresh token successfully." : "Token is still valid.";

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, message, responseData)
        );
    }

    @GetMapping("/get-all")
    @PreAuthorize("hasRole('" + Constants.USER + "') or hasRole('" + Constants.ADMIN + "')")
    public List<UserDTO> getAllUser() {

        logger.info("----------Web Cinema: List User----------");

        return userService.getAllUser();
    }

    @GetMapping("/find-by-id")
    @PreAuthorize("hasRole('" + Constants.USER + "') or hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<ResponseObject> findById(@Valid @RequestParam Long id) {

        logger.info("----------Web Cinema: Get User----------");

        UserDTO responseData = userService.findById(id);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Find user successfully.", responseData)
        );
    }
}
