package cyberpulse.auth.service;

import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.time.Instant;

/**
 * RFC 6238 TOTP (6-digit, 30s period, HMAC-SHA1) helper with Base32 encoding
 * and +-1 step clock-skew tolerance.
 */
@Service
public class TotpService {

    private static final String BASE32_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
    private static final int SECRET_BYTES = 20;
    private static final int TIME_STEP_SECONDS = 30;
    private static final int DIGITS = 6;
    private static final SecureRandom RANDOM = new SecureRandom();

    public String generateSecret() {
        byte[] bytes = new byte[SECRET_BYTES];
        RANDOM.nextBytes(bytes);
        return base32Encode(bytes);
    }

    public String buildOtpAuthUri(String username, String secret) {
        String encodedIssuer = URLEncoder.encode("CyberPulse", StandardCharsets.UTF_8);
        String encodedAccount = URLEncoder.encode(username, StandardCharsets.UTF_8);
        return "otpauth://totp/" + encodedIssuer + ":" + encodedAccount
                + "?secret=" + secret + "&issuer=" + encodedIssuer + "&digits=6&period=30";
    }

    public boolean verifyCode(String base32Secret, String code) {
        return verifyCode(base32Secret, code, Instant.now().getEpochSecond());
    }

    public boolean verifyCode(String base32Secret, String code, long epochSeconds) {
        if (base32Secret == null || base32Secret.isBlank() || code == null) {
            return false;
        }
        String normalized = code.trim();
        if (!normalized.matches("\\d{6}")) {
            return false;
        }

        byte[] key = base32Decode(base32Secret);
        long currentBucket = epochSeconds / TIME_STEP_SECONDS;

        for (long offset = -1; offset <= 1; offset++) {
            String expected = generateCode(key, currentBucket + offset);
            if (expected.equals(normalized)) {
                return true;
            }
        }
        return false;
    }

    public String generateCodeForEpoch(String base32Secret, long epochSeconds) {
        return generateCode(base32Decode(base32Secret), epochSeconds / TIME_STEP_SECONDS);
    }

    private String generateCode(byte[] key, long timeStep) {
        try {
            byte[] data = ByteBuffer.allocate(8).putLong(timeStep).array();
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(key, "HmacSHA1"));
            byte[] hash = mac.doFinal(data);

            int offset = hash[hash.length - 1] & 0x0F;
            int binary = ((hash[offset] & 0x7F) << 24)
                    | ((hash[offset + 1] & 0xFF) << 16)
                    | ((hash[offset + 2] & 0xFF) << 8)
                    | (hash[offset + 3] & 0xFF);
            int otp = binary % (int) Math.pow(10, DIGITS);
            return String.format("%06d", otp);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Failed to compute TOTP", e);
        }
    }

    private static String base32Encode(byte[] data) {
        StringBuilder sb = new StringBuilder();
        int buffer = 0;
        int bitsLeft = 0;
        for (byte b : data) {
            buffer = (buffer << 8) | (b & 0xFF);
            bitsLeft += 8;
            while (bitsLeft >= 5) {
                sb.append(BASE32_ALPHABET.charAt((buffer >> (bitsLeft - 5)) & 0x1F));
                bitsLeft -= 5;
            }
        }
        if (bitsLeft > 0) {
            sb.append(BASE32_ALPHABET.charAt((buffer << (5 - bitsLeft)) & 0x1F));
        }
        return sb.toString();
    }

    private static byte[] base32Decode(String base32) {
        String cleaned = base32.trim().replace("=", "").toUpperCase();
        byte[] out = new byte[(cleaned.length() * 5) / 8];
        int buffer = 0;
        int bitsLeft = 0;
        int index = 0;
        for (char c : cleaned.toCharArray()) {
            int val = BASE32_ALPHABET.indexOf(c);
            if (val < 0) {
                throw new IllegalArgumentException("Invalid Base32 character: " + c);
            }
            buffer = (buffer << 5) | val;
            bitsLeft += 5;
            if (bitsLeft >= 8) {
                out[index++] = (byte) ((buffer >> (bitsLeft - 8)) & 0xFF);
                bitsLeft -= 8;
            }
        }
        return out;
    }
}
