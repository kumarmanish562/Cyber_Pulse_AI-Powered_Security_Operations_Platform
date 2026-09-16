package cyberpulse.auth.security;


import cyberpulse.auth.entity.RefreshToken;
import cyberpulse.auth.repository.RefreshTokenRepository;
import cyberpulse.user.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    private final long refreshTokenExpiration;

    private final SecureRandom secureRandom =
            new SecureRandom();

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            @Value("${jwt.refresh-token-expiration}")
            long refreshTokenExpiration
    ) {

        this.refreshTokenRepository =
                refreshTokenRepository;

        this.refreshTokenExpiration =
                refreshTokenExpiration;
    }

    @Transactional
    public String createRefreshToken(
            User user
    ) {

        byte[] randomBytes = new byte[64];

        secureRandom.nextBytes(randomBytes);

        String rawToken =
                Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString(randomBytes);

        RefreshToken refreshToken =
                new RefreshToken();

        refreshToken.setUser(user);

        refreshToken.setTokenHash(
                hashToken(rawToken)
        );

        refreshToken.setExpiresAt(
                Instant.now().plusMillis(
                        refreshTokenExpiration
                )
        );

        refreshToken.setRevoked(false);

        refreshTokenRepository.save(
                refreshToken
        );

        return rawToken;
    }

    @Transactional(readOnly = true)
    public RefreshToken validate(
            String rawToken
    ) {

        String hash =
                hashToken(rawToken);

        RefreshToken refreshToken =
                refreshTokenRepository
                        .findByTokenHash(hash)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Invalid refresh token"
                                )
                        );

        if (refreshToken.isRevoked()) {

            throw new IllegalArgumentException(
                    "Refresh token has been revoked"
            );
        }

        if (
                refreshToken.getExpiresAt()
                        .isBefore(Instant.now())
        ) {

            throw new IllegalArgumentException(
                    "Refresh token has expired"
            );
        }

        return refreshToken;
    }

    @Transactional
    public void revoke(
            String rawToken
    ) {

        RefreshToken refreshToken =
                validate(rawToken);

        refreshToken.setRevoked(true);

        refreshTokenRepository.save(
                refreshToken
        );
    }

    @Transactional
    public String rotate(
            String oldRawToken
    ) {

        RefreshToken oldToken =
                validate(oldRawToken);

        oldToken.setRevoked(true);

        refreshTokenRepository.save(oldToken);

        return createRefreshToken(
                oldToken.getUser()
        );
    }

    private String hashToken(
            String token
    ) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash =
                    digest.digest(
                            token.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            StringBuilder hex =
                    new StringBuilder();

            for (byte b : hash) {

                hex.append(
                        String.format(
                                "%02x",
                                b
                        )
                );
            }

            return hex.toString();

        } catch (NoSuchAlgorithmException e) {

            throw new IllegalStateException(
                    "SHA-256 algorithm unavailable",
                    e
            );
        }
    }
}