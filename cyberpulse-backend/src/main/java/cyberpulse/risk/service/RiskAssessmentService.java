package cyberpulse.risk.service;

import cyberpulse.detection.entity.ThreatDetection;
import cyberpulse.detection.repository.ThreatDetectionRepository;
import cyberpulse.risk.dto.RiskAssessmentResponse;
import cyberpulse.risk.engine.RiskCalculationResult;
import cyberpulse.risk.engine.RiskCalculator;
import cyberpulse.risk.entity.RiskAssessment;
import cyberpulse.risk.entity.RiskSeverity;
import cyberpulse.risk.mapper.RiskAssessmentMapper;
import cyberpulse.risk.repository.RiskAssessmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class RiskAssessmentService {

    private final RiskAssessmentRepository
            riskAssessmentRepository;

    private final ThreatDetectionRepository
            threatDetectionRepository;

    private final RiskCalculator riskCalculator;

    private final RiskAssessmentMapper mapper;

    @Transactional
    public RiskAssessmentResponse assess(
            UUID threatDetectionId
    ) {

        if (riskAssessmentRepository
                .existsByThreatDetectionId(
                        threatDetectionId
                )) {

            return getByThreatDetectionId(
                    threatDetectionId
            );
        }

        ThreatDetection detection =
                threatDetectionRepository
                        .findById(threatDetectionId)
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Threat detection not found"
                                )
                        );

        RiskCalculationResult result =
                riskCalculator.calculate(
                        detection,
                        detection.getEvent()
                );

        RiskAssessment assessment =
                new RiskAssessment();

        assessment.setThreatDetection(
                detection
        );

        assessment.setRiskScore(
                result.riskScore()
        );

        assessment.setSeverity(
                result.severity()
        );

        assessment.setBaseScore(
                result.baseScore()
        );

        assessment.setConfidenceScore(
                result.confidenceScore()
        );

        assessment.setFrequencyScore(
                result.frequencyScore()
        );

        assessment.setSeverityScore(
                result.severityScore()
        );

        assessment.setFactors(
                result.factors()
        );

        RiskAssessment saved =
                riskAssessmentRepository.save(
                        assessment
                );

        return mapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public RiskAssessmentResponse getById(
            UUID id
    ) {

        return riskAssessmentRepository
                .findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Risk assessment not found"
                        )
                );
    }

    @Transactional(readOnly = true)
    public RiskAssessmentResponse
    getByThreatDetectionId(
            UUID threatDetectionId
    ) {

        return riskAssessmentRepository
                .findByThreatDetectionId(
                        threatDetectionId
                )
                .map(mapper::toResponse)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Risk assessment not found"
                        )
                );
    }

    @Transactional(readOnly = true)
    public Page<RiskAssessmentResponse> getAll(
            Pageable pageable
    ) {

        return riskAssessmentRepository
                .findAll(pageable)
                .map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<RiskAssessmentResponse>
    getBySeverity(
            RiskSeverity severity,
            Pageable pageable
    ) {

        return riskAssessmentRepository
                .findBySeverity(
                        severity,
                        pageable
                )
                .map(mapper::toResponse);
    }
}