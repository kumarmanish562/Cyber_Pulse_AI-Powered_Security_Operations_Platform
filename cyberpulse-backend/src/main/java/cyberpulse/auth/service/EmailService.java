package cyberpulse.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final String fromAddress;
    private final String mailHost;

    public EmailService(
            ObjectProvider<JavaMailSender> mailSenderProvider,
            @Value("${cyberpulse.mail.from:noreply@cyberpulse.local}") String fromAddress,
            @Value("${spring.mail.host:}") String mailHost
    ) {
        this.mailSender = mailSenderProvider.getIfAvailable();
        this.fromAddress = fromAddress;
        this.mailHost = mailHost != null ? mailHost.trim() : "";
    }

    public void sendPasswordResetEmail(String toEmail, String resetUrl) {
        if (mailSender == null || mailHost.isEmpty()) {
            log.info("SMTP not configured; password reset link for {} -> {}", toEmail, resetUrl);
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(toEmail);
        message.setSubject("CyberPulse — Password Reset Request");
        message.setText("""
                A password reset was requested for your CyberPulse SOC account.

                Reset your password using the link below (valid for 30 minutes):
                %s

                If you did not request this, you can safely ignore this email.
                """.formatted(resetUrl));

        mailSender.send(message);
    }
}
