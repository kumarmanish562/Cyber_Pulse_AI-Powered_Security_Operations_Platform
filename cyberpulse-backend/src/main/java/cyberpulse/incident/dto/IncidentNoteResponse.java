package cyberpulse.incident.dto;

import java.time.Instant;
import java.util.UUID;

public record IncidentNoteResponse(
        UUID id,
        UUID incidentId,
        UUID authorId,
        String content,
        Instant createdAt
) {
}