package vi.wbca.webcinema.controller;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vi.wbca.webcinema.dto.UserDTO;
import vi.wbca.webcinema.groupValidate.InsertUser;
import vi.wbca.webcinema.groupValidate.LoginUser;
import vi.wbca.webcinema.model.User;
import vi.wbca.webcinema.service.accountService.AccountService;
import vi.wbca.webcinema.service.userService.UserService;
import vi.wbca.webcinema.util.Informations;
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

    @PostMapping("/register")
    public ResponseEntity<ResponseObject> register(@Validated(InsertUser.class) @RequestBody UserDTO request) {

        logger.info("----------Web Cinema: Insert New User----------");

        userService.register(request);

        Map<String, String> responseData = new HashMap<>();
        responseData.put(Informations.USER_NAME, request.getUserName());
        responseData.put(Informations.EMAIL, request.getEmail());
        responseData.put(Informations.LIST_ROLE, request.getListRoles().toString());

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

    @GetMapping("/get-all")
    public List<User> getAllUser() {
        logger.info("----------Web Cinema: List User----------");
        return userService.getAllUser();
    }

    @GetMapping("/find-by-user-name")
    public ResponseEntity<ResponseObject> findByUserName(@Valid @RequestParam String userName) {
        User responseData = userService.findByUserName(userName);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK, "Find user successfully", responseData)
        );
    }
}
