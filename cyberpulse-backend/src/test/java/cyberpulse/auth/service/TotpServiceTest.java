package cyberpulse.auth.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TotpServiceTest {

    private final TotpService totpService = new TotpService();

    @Test
    void shouldGenerateSecretAndVerifyValidCode() {
        String secret = totpService.generateSecret();
        assertNotNull(secret);
        assertFalse(secret.isBlank());

        long epoch = 1_700_000_000L;
        String validCode = totpService.generateCodeForEpoch(secret, epoch);

        assertTrue(totpService.verifyCode(secret, validCode, epoch));
        assertTrue(totpService.verifyCode(secret, validCode, epoch + 30));
        assertFalse(totpService.verifyCode(secret, "000000", epoch));
        assertFalse(totpService.verifyCode(secret, "abc", epoch));
    }

    @Test
    void shouldBuildOtpAuthUri() {
        String uri = totpService.buildOtpAuthUri("analyst", "JBSWY3DPEHPK3PXP");
        assertTrue(uri.startsWith("otpauth://totp/CyberPulse:analyst?secret=JBSWY3DPEHPK3PXP"));
    }
}
