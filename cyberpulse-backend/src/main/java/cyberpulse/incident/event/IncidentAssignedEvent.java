package cyberpulse.incident.event;

import java.util.UUID;

public record IncidentAssignedEvent(

        UUID incidentId,

        UUID previousAssignee,

        UUID newAssignee

) {
}