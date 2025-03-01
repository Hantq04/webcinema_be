package vi.wbca.webcinema.util;

import vi.wbca.webcinema.model.User;

public class EmailUtils {
    public static String getEmailMessage(User user, String otp) {
        return String.format(
                "<p>Hi, %s,</p>" +
                        "<p>Thank you for registering with us.</p>" +
                        "<p>Your One-Time Password (OTP) for verification is:</p>" +
                        "<h2>%s</h2>" +
                        "<p>Please enter this OTP to activate your account. This OTP is valid for a limited time.</p>" +
                        "<p>Thank you <br> Support Team</p>",
                user, otp
        );
    }
}
