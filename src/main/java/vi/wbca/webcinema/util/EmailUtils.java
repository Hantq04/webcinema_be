package vi.wbca.webcinema.util;

import vi.wbca.webcinema.model.user.User;

public class EmailUtils {
    public static String getEmailMessage(User user, String otp) {
        return String.format(
                "<p>Hi, %s,</p>" +
                        "<p>Thank you for registering with us.</p>" +
                        "<p>Your One-Time Password (OTP) for verification is:</p>" +
                        "<h2>%s</h2>" +
                        "<p>Please enter this OTP to activate your account. This OTP is valid for a limited time.</p>" +
                        "<p>Thank you, <br>FilmTick Support Team</p>",
                user.getName(), otp
        );
    }

    public static String getResendEmailMessage(String otp) {
        return String.format(
                "<p>You have requested to resend your One-Time Password (OTP) for verification.</p>" +
                        "<p>Your new OTP is:</p>" +
                        "<h2>%s</h2>" +
                        "<p>Please enter this OTP to verify your account. This OTP is valid for a limited time.</p>" +
                        "<p>Thank you,<br>FilmTick Support Team</p>",
                otp
        );
    }

    public static String getChangePasswordMessage(String otp) {
        return String.format(
                "<p>You have requested to change your password.</p>" +
                        "<p>Your One-Time Password (OTP) for changing your password is:</p>" +
                        "<h2> %s </h2>" +
                        "<p>Please enter this OTP to proceed with changing your password. This OTP is valid for a limited time.</p>" +
                        "<p>Thank you,<br>FilmTick Support Team</p>",
                otp
        );
    }

    public static String getConfirmPaymentMessage(String detail) {
        return String.format(
                "<h3>Thank you for your payment!</h3>" +
                        "<p>Your order has been successfully processed. Below are your transaction details:</p>" +
                        "<ul>%s</ul>" +
                        "<p>If you have any questions, feel free to contact our support team.</p>" +
                        "<p>Regards,<br>FilmTick Support Team</p>",
                detail
        );
    }
}
