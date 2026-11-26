package cyberpulse.apikey.dto;

public record CreateApiKeyResponse(
        ApiKeyResponse apiKey,
        String rawKey
) {
}
