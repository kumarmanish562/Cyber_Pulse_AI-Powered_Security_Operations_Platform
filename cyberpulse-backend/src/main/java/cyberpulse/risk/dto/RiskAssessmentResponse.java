package cyberpulse.risk.dto;


import cyberpulse.risk.entity.RiskSeverity;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record RiskAssessmentResponse(

        UUID id,

        UUID threatDetectionId,

        UUID eventId,

        UUID ruleId,

        String ruleName,

        Integer riskScore,

        RiskSeverity severity,

        Integer baseScore,

        Integer confidenceScore,

        Integer frequencyScore,

        Integer severityScore,

        Map<String, Object> factors,

        Instant assessedAt
) {
}