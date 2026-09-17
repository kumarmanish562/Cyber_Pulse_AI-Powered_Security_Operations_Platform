package cyberpulse.incident.repository;

import cyberpulse.incident.entity.IncidentEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IncidentEventRepository
        extends JpaRepository<IncidentEvent, UUID> {

    List<IncidentEvent> findByIncident_Id(UUID incidentId);

    boolean existsByIncident_IdAndEvent_Id(
            UUID incidentId,
            UUID eventId
    );
}