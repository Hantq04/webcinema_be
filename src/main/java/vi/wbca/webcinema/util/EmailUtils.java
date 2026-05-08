package vi.wbca.webcinema.util;

public class EmailUtils {
    public static String getEmailMessage(String displayName, String otp) {
        return String.format(
                "<p>Hi, %s,</p>" +
                        "<p>Thank you for registering with us.</p>" +
                        "<p>Your One-Time Password (OTP) for verification is:</p>" +
                        "<h2>%s</h2>" +
                        "<p>Please enter this OTP to activate your account. This OTP is valid for a limited time.</p>" +
                        "<p>Thank you, <br>CineGo Support Team</p>",
                displayName, otp
        );
    }

    public static String getResendEmailMessage(String otp) {
        return String.format(
                "<p>You have requested to resend your One-Time Password (OTP) for verification.</p>" +
                        "<p>Your new OTP is:</p>" +
                        "<h2>%s</h2>" +
                        "<p>Please enter this OTP to verify your account. This OTP is valid for a limited time.</p>" +
                        "<p>Thank you,<br>CineGo Support Team</p>",
                otp
        );
    }

    public static String getChangePasswordMessage(String otp) {
        return String.format(
                "<p>You have requested to change your password.</p>" +
                        "<p>Your One-Time Password (OTP) for changing your password is:</p>" +
                        "<h2> %s </h2>" +
                        "<p>Please enter this OTP to proceed with changing your password. This OTP is valid for a limited time.</p>" +
                        "<p>Thank you,<br>CineGo Support Team</p>",
                otp
        );
    }

        public static String getConfirmPaymentMessage(String detail, String printTicketUrl) {
        return String.format(
                "<h3>Thank you for your payment!</h3>" +
                        "<p>Your order has been successfully processed. Below are your transaction details:</p>" +
                        "<div style=\"padding-left:20px;\">" +
                        "<ul style=\"margin:0; padding-left:18px;\">%s</ul>" +
                        "<div style=\"margin:8px 0 14px 70px;\">" +
                        "<a href=\"%s\" style=\"display:inline-block;background:#f3f4f6;color:#374151;text-decoration:none;padding:8px 16px;border:1px solid #d1d5db;border-radius:6px;font-weight:600;line-height:1.1;\">In vé</a>" +
                        "</div>" +
                        "</div>" +
                        "<p>If you have any questions, feel free to contact our support team.</p>" +
                        "<p>Regards,<br>CineGo Support Team</p>",
                detail, printTicketUrl
        );
    }
}
