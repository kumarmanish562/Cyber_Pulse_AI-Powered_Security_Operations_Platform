package cyberpulse.incident.dto;

import java.time.Instant;
import java.util.UUID;

public record IncidentEventResponse(
        UUID id,
        UUID incidentId,
        UUID eventId,
        Instant createdAt
) {
}