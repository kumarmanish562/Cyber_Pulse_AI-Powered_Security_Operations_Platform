package cyberpulse.incident.mapper;

import cyberpulse.incident.dto.IncidentEventResponse;
import cyberpulse.incident.entity.IncidentEvent;
import org.springframework.stereotype.Component;

@Component
public class IncidentEventMapper {

    public IncidentEventResponse toResponse(IncidentEvent incidentEvent) {

        return new IncidentEventResponse(
                incidentEvent.getId(),
                incidentEvent.getIncident().getId(),
                incidentEvent.getEvent().getId(),
                incidentEvent.getCreatedAt()
        );
    }
}