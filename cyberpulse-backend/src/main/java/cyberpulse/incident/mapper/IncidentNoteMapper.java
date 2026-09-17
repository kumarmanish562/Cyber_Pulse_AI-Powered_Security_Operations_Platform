package cyberpulse.incident.mapper;

import cyberpulse.incident.dto.IncidentNoteResponse;
import cyberpulse.incident.entity.IncidentNote;
import org.springframework.stereotype.Component;

@Component
public class IncidentNoteMapper {

    public IncidentNoteResponse toResponse(IncidentNote incidentNote) {

        return new IncidentNoteResponse(
                incidentNote.getId(),
                incidentNote.getIncident().getId(),
                incidentNote.getUser().getId(),
                incidentNote.getNote(),
                incidentNote.getCreatedAt()
        );
    }
}