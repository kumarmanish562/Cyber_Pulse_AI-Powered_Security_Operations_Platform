package cyberpulse.incident.event;

import java.util.UUID;

public record IncidentNoteAddedEvent(

        UUID incidentId,

        UUID noteId

) {
}