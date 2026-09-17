package cyberpulse.risk.repository;

import cyberpulse.risk.entity.RiskAssessment;
import cyberpulse.risk.entity.RiskSeverity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RiskAssessmentRepository
        extends JpaRepository<RiskAssessment, UUID> {

    Optional<RiskAssessment>
    findByThreatDetectionId(UUID threatDetectionId);

    boolean existsByThreatDetectionId(
            UUID threatDetectionId
    );

    Page<RiskAssessment>
    findBySeverity(
            RiskSeverity severity,
            Pageable pageable
    );

    Page<RiskAssessment>
    findByRiskScoreGreaterThanEqual(
            Integer score,
            Pageable pageable
    );
}