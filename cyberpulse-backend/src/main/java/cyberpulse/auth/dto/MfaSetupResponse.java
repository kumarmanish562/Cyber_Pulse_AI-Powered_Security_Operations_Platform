package cyberpulse.auth.dto;

public record MfaSetupResponse(
        String secret,
        String otpauthUri
) {
}
