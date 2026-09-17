package cyberpulse.incident.dto;

import cyberpulse.incident.entity.IncidentSeverity;
import cyberpulse.incident.entity.IncidentStatus;

import java.time.Instant;
import java.util.UUID;

public record IncidentResponse(
        UUID id,
        String incidentNumber,
        String title,
        String description,
        IncidentStatus status,
        IncidentSeverity severity,
        UUID riskAssessmentId,
        UUID assignedTo,
        Instant createdAt,
        Instant updatedAt,
        Instant resolvedAt,
        Instant closedAt
) {
}