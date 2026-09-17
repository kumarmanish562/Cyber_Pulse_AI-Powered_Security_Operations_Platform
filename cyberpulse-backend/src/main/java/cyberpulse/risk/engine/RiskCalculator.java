package cyberpulse.risk.engine;

import cyberpulse.detection.entity.ThreatDetection;
import cyberpulse.event.entity.SecurityEvent;
import cyberpulse.risk.entity.RiskSeverity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RiskCalculator {

    public RiskCalculationResult calculate(
            ThreatDetection detection,
            SecurityEvent event
    ) {

        int baseScore = calculateBaseScore(event);

        int confidenceScore =
                calculateConfidenceScore(detection);

        int frequencyScore =
                calculateFrequencyScore(detection);

        int severityScore =
                calculateSeverityScore(event);

        int totalScore = clamp(
                baseScore
                        + confidenceScore
                        + frequencyScore
                        + severityScore
        );

        RiskSeverity severity =
                determineSeverity(totalScore);

        Map<String, Object> factors =
                new HashMap<>();

        factors.put("baseScore", baseScore);
        factors.put("confidenceScore", confidenceScore);
        factors.put("frequencyScore", frequencyScore);
        factors.put("severityScore", severityScore);
        factors.put("totalScore", totalScore);

        if (event.getSeverity() != null) {
            factors.put(
                    "eventSeverity",
                    event.getSeverity().name()
            );
        }

        if (detection.getRule() != null) {

            if (detection.getRule().getRuleType() != null) {

                factors.put(
                        "ruleType",
                        detection.getRule()
                                .getRuleType()
                                .name()
                );
            }
        }

        return new RiskCalculationResult(
                totalScore,
                severity,
                baseScore,
                confidenceScore,
                frequencyScore,
                severityScore,
                factors
        );
    }

    private int calculateBaseScore(
            SecurityEvent event
    ) {

        if (event.getSeverity() == null) {
            return 0;
        }

        return switch (event.getSeverity()) {

            case LOW -> 10;

            case MEDIUM -> 20;

            case HIGH -> 30;

            case CRITICAL -> 40;
        };
    }

    private int calculateConfidenceScore(
            ThreatDetection detection
    ) {

        if (detection.getConfidenceScore() == null) {
            return 0;
        }

        double confidence =
                detection.getConfidenceScore()
                        .doubleValue();

        return (int) Math.round(
                confidence * 0.20
        );
    }

    private int calculateFrequencyScore(
            ThreatDetection detection
    ) {

        if (detection.getRule() == null) {
            return 0;
        }

        if (detection.getRule().getRuleType() == null) {
            return 0;
        }

        String ruleType =
                detection.getRule()
                        .getRuleType()
                        .name();

        if ("BRUTE_FORCE".equals(ruleType)) {
            return 20;
        }

        return 5;
    }

    private int calculateSeverityScore(
            SecurityEvent event
    ) {

        if (event.getSeverity() == null) {
            return 0;
        }

        return switch (event.getSeverity()) {

            case LOW -> 0;

            case MEDIUM -> 5;

            case HIGH -> 10;

            case CRITICAL -> 20;
        };
    }

    private int clamp(int score) {

        return Math.max(
                0,
                Math.min(score, 100)
        );
    }

    private RiskSeverity determineSeverity(
            int score
    ) {

        if (score >= 75) {
            return RiskSeverity.CRITICAL;
        }

        if (score >= 50) {
            return RiskSeverity.HIGH;
        }

        if (score >= 25) {
            return RiskSeverity.MEDIUM;
        }

        return RiskSeverity.LOW;
    }
}