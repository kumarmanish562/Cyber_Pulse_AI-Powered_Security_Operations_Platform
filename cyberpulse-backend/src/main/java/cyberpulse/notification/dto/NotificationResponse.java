package cyberpulse.notification.dto;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(

        UUID id,

        String title,

        String message,

        String type,

        String entityType,

        UUID entityId,

        boolean read,

        Instant createdAt

) {
}