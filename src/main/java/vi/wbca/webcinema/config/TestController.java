package vi.wbca.webcinema.config;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vi.wbca.webcinema.util.EmailUtils;
import vi.wbca.webcinema.util.Constants;

import java.io.UnsupportedEncodingException;
import jakarta.mail.MessagingException;

import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/test")
public class TestController {
    private static final Logger logger = Logger.getLogger(TestController.class.getName());
    private final EmailService emailService;

    @GetMapping("/all")
    public String allAccess() {
        logger.info("----------All Role----------");
        return "Public Content";
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('" + Constants.USER + "') or hasRole('" + Constants.ADMIN + "')")
    public String userAccess() {
        logger.info("----------User Role----------");
        return "User Content";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('" + Constants.ADMIN + "')")
    public String adminAccess() {
        logger.info("----------Admin Role----------");
        return "Admin Board";
    }

    @PostMapping("/send-payment-email")
    @PreAuthorize("hasRole('" + Constants.ADMIN + "')")
    public ResponseEntity<String> sendPaymentEmail(@RequestParam String email)
            throws MessagingException, UnsupportedEncodingException {
        logger.info("----------Test Send Payment Email----------");
        String subject = "VNPay Payment Response - Test";
        String body = EmailUtils.getConfirmPaymentMessage(null, null);
        emailService.sendMail(email, subject, body);
        return ResponseEntity.status(HttpStatus.OK).body("Email sent");
    }
}
