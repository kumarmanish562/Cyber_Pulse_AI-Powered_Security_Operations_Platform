package cyberpulse.audit.dto;

import java.time.Instant;
import java.util.UUID;

public record AuditLogResponse(

        UUID id,

        UUID userId,

        String action,

        String entityType,

        UUID entityId,

        String ipAddress,

        String userAgent,

        String details,

        Instant createdAt

) {
}