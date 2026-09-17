package cyberpulse.incident.repository;

import cyberpulse.incident.entity.IncidentNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IncidentNoteRepository
        extends JpaRepository<IncidentNote, UUID> {

    List<IncidentNote> findByIncidentIdOrderByCreatedAtAsc(
            UUID incidentId
    );
}