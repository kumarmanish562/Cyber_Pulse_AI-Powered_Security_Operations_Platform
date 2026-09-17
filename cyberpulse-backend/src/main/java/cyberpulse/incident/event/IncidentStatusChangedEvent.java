package cyberpulse.incident.event;

import cyberpulse.incident.entity.IncidentStatus;

import java.util.UUID;

public record IncidentStatusChangedEvent(

        UUID incidentId,

        IncidentStatus oldStatus,

        IncidentStatus newStatus

) {
}