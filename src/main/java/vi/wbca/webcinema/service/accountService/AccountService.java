package vi.wbca.webcinema.service.accountService;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vi.wbca.webcinema.config.EmailService;
import vi.wbca.webcinema.enums.EUserStatus;
import vi.wbca.webcinema.exception.AppException;
import vi.wbca.webcinema.exception.ErrorCode;
import vi.wbca.webcinema.model.ConfirmEmail;
import vi.wbca.webcinema.model.User;
import vi.wbca.webcinema.model.UserStatus;
import vi.wbca.webcinema.repository.ConfirmEmailRepo;
import vi.wbca.webcinema.repository.UserRepo;
import vi.wbca.webcinema.repository.UserStatusRepo;
import vi.wbca.webcinema.util.EmailUtils;
import vi.wbca.webcinema.util.generate.GenerateOTP;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final EmailService emailService;
    private final ConfirmEmailRepo confirmEmailRepo;
    private final UserRepo userRepo;
    private final UserStatusRepo userStatusRepo;
    private final PasswordEncoder passwordEncoder;
    private final String generateOTP = GenerateOTP.generateOTP();
    @Value("${application.email.verify-expiration}")
    private long expiredTime;

    public void sendVerificationEmail(User user)
            throws MessagingException, UnsupportedEncodingException {
        String subject = "Email Verification";
        String content = EmailUtils.getEmailMessage(user, generateOTP);
        emailService.sendMail(user.getEmail(), subject, content);
        createConfirmEmail(user);
    }

    public void createConfirmEmail(User user) {
        ConfirmEmail confirmEmail = new ConfirmEmail();
        confirmEmail.setUser(user);
        confirmEmail.setRequiredTime(new Date());
        confirmEmail.setExpiredTime(new Date(System.currentTimeMillis() + expiredTime));
        confirmEmail.setConfirmCode(generateOTP);
        confirmEmailRepo.save(confirmEmail);
    }

    public String validateToken(String otp) {
        ConfirmEmail code = confirmEmailRepo.findByConfirmCode(otp)
                .orElseThrow(() -> new AppException(ErrorCode.OTP_NOT_FOUND));
        User user = userRepo.findByConfirmEmails(code)
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_FOUND));
        if (user.isActive()) {
            throw new AppException(ErrorCode.USER_ACTIVE);
        }
        Calendar calendar = Calendar.getInstance();
        if (code.getExpiredTime().before(calendar.getTime()) && !code.isConfirm()) {
            confirmEmailRepo.delete(code);
            throw new AppException(ErrorCode.EXPIRED_OTP);
        }
        user.setActive(true);
        code.setConfirm(true);

        UserStatus userStatus = userStatusRepo.findByCode(EUserStatus.ACTIVE.name());
        user.setUserStatus(userStatus);

        userRepo.save(user);
        confirmEmailRepo.save(code);
        return "Now you can login to your account.";
    }

    public String resendVerificationEmail(String email) throws MessagingException, UnsupportedEncodingException {
        String subject = "Email Verification";
        String content = EmailUtils.getResendEmailMessage(generateOTP);
        emailService.sendMail(email, subject, content);
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_FOUND));
        createConfirmEmail(user);
        return "Please verify your account within 5 minutes.";
    }

    public String changePassword(String otp, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new AppException(ErrorCode.PASSWORD_MISMATCH);
        }
        ConfirmEmail code = confirmEmailRepo.findByConfirmCode(otp)
                .orElseThrow(() -> new AppException(ErrorCode.OTP_NOT_FOUND));
        Calendar calendar = Calendar.getInstance();
        if (code.getExpiredTime().before(calendar.getTime()) && !code.isConfirm()) {
            confirmEmailRepo.delete(code);
            throw new AppException(ErrorCode.EXPIRED_OTP);
        }
        User user = userRepo.findByConfirmEmails(code)
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_FOUND));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
        return "Password change successfully. You can now log in with your new password.";
    }

    public String sendChangePassword(String email) throws MessagingException, UnsupportedEncodingException {
        String subject = "Email Verification";
        String content = EmailUtils.getChangePasswordMessage(generateOTP);
        emailService.sendMail(email, subject, content);
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_FOUND));
        createConfirmEmail(user);
        return "Check your email for the password change OTP.";
    }
}
