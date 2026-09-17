package cyberpulse.incident.mapper;

import cyberpulse.incident.dto.IncidentResponse;
import cyberpulse.incident.entity.Incident;
import org.springframework.stereotype.Component;

@Component
public class IncidentMapper {

    public IncidentResponse toResponse(Incident incident) {

        return new IncidentResponse(
                incident.getId(),
                incident.getIncidentNumber(),
                incident.getTitle(),
                incident.getDescription(),
                incident.getStatus(),
                incident.getSeverity(),
                incident.getRiskAssessment().getId(),
                incident.getAssignedTo(),
                incident.getCreatedAt(),
                incident.getUpdatedAt(),
                incident.getResolvedAt(),
                incident.getClosedAt()
        );
    }
}