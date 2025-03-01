package vi.wbca.webcinema.service.registrationService;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.config.EmailService;
import vi.wbca.webcinema.model.ConfirmEmail;
import vi.wbca.webcinema.model.User;
import vi.wbca.webcinema.util.EmailUtils;
import vi.wbca.webcinema.util.GenerateOTP;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final EmailService emailService;

    public void sendVerificationEmail(User user)
            throws MessagingException, UnsupportedEncodingException {
        String subject = "Email Verification";
        String generateOTP = GenerateOTP.generateOTP();
        String content = EmailUtils.getEmailMessage(user, generateOTP);
        emailService.sendMail(user.getEmail(), subject, content);
    }

    public ConfirmEmail confirmEmail(User user) {
        ConfirmEmail confirmEmail = new ConfirmEmail();
        confirmEmail.setUser(user);
        confirmEmail.getConfirmCode();
        return confirmEmail;
    }
}
