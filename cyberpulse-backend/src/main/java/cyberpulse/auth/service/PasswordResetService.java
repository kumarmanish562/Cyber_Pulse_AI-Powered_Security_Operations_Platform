package cyberpulse.auth.service;

import cyberpulse.auth.entity.PasswordResetToken;
import cyberpulse.auth.repository.PasswordResetTokenRepository;
import cyberpulse.auth.repository.RefreshTokenRepository;
import cyberpulse.user.entity.User;
import cyberpulse.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;

@Service
public class PasswordResetService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final long TOKEN_EXPIRY_MINUTES = 30;

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final String frontendUrl;

    public PasswordResetService(
            UserRepository userRepository,
            PasswordResetTokenRepository tokenRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService,
            @Value("${cyberpulse.frontend-url:http://localhost:5173}") String frontendUrl
    ) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.frontendUrl = frontendUrl.replaceAll("/+$", "");
    }

    @Transactional
    public void requestReset(String email) {
        userRepository.findByEmailIgnoreCase(email.trim().toLowerCase())
                .ifPresent(user -> {
                    byte[] bytes = new byte[32];
                    RANDOM.nextBytes(bytes);
                    String rawToken = HexFormat.of().formatHex(bytes);

                    PasswordResetToken entity = new PasswordResetToken();
                    entity.setUser(user);
                    entity.setTokenHash(sha256Hex(rawToken));
                    entity.setExpiresAt(Instant.now().plus(TOKEN_EXPIRY_MINUTES, ChronoUnit.MINUTES));
                    entity.setUsed(false);
                    tokenRepository.save(entity);

                    String resetUrl = frontendUrl + "/reset-password?token=" + rawToken;
                    emailService.sendPasswordResetEmail(user.getEmail(), resetUrl);
                });
    }

    @Transactional
    public void resetPassword(String rawToken, String newPassword) {
        String hash = sha256Hex(rawToken.trim());
        PasswordResetToken resetToken = tokenRepository.findByTokenHashAndUsedFalse(hash)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired password reset token"));

        if (resetToken.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Password reset token has expired");
        }

        User user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        refreshTokenRepository.revokeAllByUserId(user.getId());
    }

    private static String sha256Hex(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }
}
