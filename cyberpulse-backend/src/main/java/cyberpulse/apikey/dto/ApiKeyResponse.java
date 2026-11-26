package cyberpulse.apikey.dto;

import java.time.Instant;
import java.util.UUID;

public record ApiKeyResponse(
        UUID id,
        String name,
        String keyPrefix,
        String scopes,
        boolean active,
        Instant lastUsedAt,
        Instant expiresAt,
        Instant createdAt
) {
}
