package cyberpulse.incident.event;

import java.util.UUID;

public record IncidentCreatedEvent(

        UUID incidentId,

        UUID riskAssessmentId,

        Integer riskScore,

        String severity

) {
}