package cyberpulse.risk.mapper;

import cyberpulse.detection.entity.ThreatDetection;
import cyberpulse.risk.dto.RiskAssessmentResponse;
import cyberpulse.risk.entity.RiskAssessment;
import org.springframework.stereotype.Component;

@Component
public class RiskAssessmentMapper {

    public RiskAssessmentResponse toResponse(
            RiskAssessment assessment
    ) {

        ThreatDetection detection =
                assessment.getThreatDetection();

        return new RiskAssessmentResponse(

                assessment.getId(),

                detection.getId(),

                detection.getEvent().getId(),

                detection.getRule().getId(),

                detection.getRule().getName(),

                assessment.getRiskScore(),

                assessment.getSeverity(),

                assessment.getBaseScore(),

                assessment.getConfidenceScore(),

                assessment.getFrequencyScore(),

                assessment.getSeverityScore(),

                assessment.getFactors(),

                assessment.getAssessedAt()
        );
    }
}