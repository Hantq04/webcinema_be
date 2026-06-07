package vi.wbca.webcinema.config;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;

    public void sendMail(String to, String subject, String content) throws MessagingException, UnsupportedEncodingException {
        sendMail(to, subject, content, null, null);
    }

    public void sendMail(String to, String subject, String content, byte[] attachment, String attachmentName)
            throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");
        messageHelper.setTo(to);
        messageHelper.setSubject(subject);
        messageHelper.setFrom("no-reply@example.com", "Support Team");
        messageHelper.setReplyTo("support@example.com");
        messageHelper.setText(content, true);
        if (attachment != null && attachmentName != null && !attachmentName.isBlank()) {
            messageHelper.addAttachment(attachmentName, new ByteArrayResource(attachment));
        }
        javaMailSender.send(message);
    }
}
