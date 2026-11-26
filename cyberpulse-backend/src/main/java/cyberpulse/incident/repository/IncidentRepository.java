package cyberpulse.incident.repository;

import cyberpulse.incident.entity.Incident;
import cyberpulse.incident.entity.IncidentSeverity;
import cyberpulse.incident.entity.IncidentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IncidentRepository
        extends JpaRepository<Incident, UUID> {

    List<Incident> findTop5ByTitleContainingIgnoreCaseOrIncidentNumberContainingIgnoreCaseOrderByCreatedAtDesc(
            String title,
            String incidentNumber
    );

    Optional<Incident> findByIncidentNumber(
            String incidentNumber
    );

    boolean existsByRiskAssessmentId(
            UUID riskAssessmentId
    );

    Page<Incident> findByStatus(
            IncidentStatus status,
            Pageable pageable
    );

    Page<Incident> findBySeverity(
            IncidentSeverity severity,
            Pageable pageable
    );

    Page<Incident> findByAssignedTo(
            UUID assignedTo,
            Pageable pageable
    );
}