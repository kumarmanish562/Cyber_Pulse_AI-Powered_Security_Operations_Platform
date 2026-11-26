package cyberpulse.blocklist.dto;

import java.time.Instant;
import java.util.UUID;

public record BlockedIpResponse(
        UUID id,
        String ipAddress,
        String reason,
        String source,
        String severity,
        boolean active,
        Instant expiresAt,
        Instant createdAt
) {
}
