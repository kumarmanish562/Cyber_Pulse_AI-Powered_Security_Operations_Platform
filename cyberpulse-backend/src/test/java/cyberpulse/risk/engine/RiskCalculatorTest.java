package cyberpulse.risk.engine;

import cyberpulse.common.enums.DetectionRuleType;
import cyberpulse.common.enums.EventType;
import cyberpulse.common.enums.Severity;
import cyberpulse.detection.entity.DetectionRule;
import cyberpulse.detection.entity.ThreatDetection;
import cyberpulse.event.entity.SecurityEvent;
import cyberpulse.risk.entity.RiskSeverity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class RiskCalculatorTest {

    private final RiskCalculator calculator =
            new RiskCalculator();

    @Test
    void shouldCalculateHighRiskForBruteForce() {

        SecurityEvent event =
                new SecurityEvent();

        event.setEventType(
                EventType.LOGIN_FAILED
        );

        event.setSeverity(
                Severity.MEDIUM
        );

        DetectionRule rule =
                new DetectionRule();

        rule.setRuleType(
                DetectionRuleType.BRUTE_FORCE
        );

        ThreatDetection detection =
                new ThreatDetection();

        detection.setRule(rule);
        detection.setEvent(event);

        detection.setConfidenceScore(
                BigDecimal.valueOf(100)
        );

        RiskCalculationResult result =
                calculator.calculate(
                        detection,
                        event
                );

        assertEquals(
                65,
                result.riskScore()
        );

        assertEquals(
                RiskSeverity.HIGH,
                result.severity()
        );
    }

    @Test
    void shouldCalculateMediumRiskForLowSeverityBruteForce() {

        SecurityEvent event =
                new SecurityEvent();

        event.setSeverity(
                Severity.LOW
        );

        DetectionRule rule =
                new DetectionRule();

        rule.setRuleType(
                DetectionRuleType.BRUTE_FORCE
        );

        ThreatDetection detection =
                new ThreatDetection();

        detection.setRule(rule);
        detection.setEvent(event);

        detection.setConfidenceScore(
                BigDecimal.ZERO
        );

        RiskCalculationResult result =
                calculator.calculate(
                        detection,
                        event
                );

        assertEquals(
                30,
                result.riskScore()
        );

        assertEquals(
                RiskSeverity.MEDIUM,
                result.severity()
        );
    }

    @Test
    void shouldCalculateCriticalRisk() {

        SecurityEvent event =
                new SecurityEvent();

        event.setSeverity(
                Severity.CRITICAL
        );

        DetectionRule rule =
                new DetectionRule();

        rule.setRuleType(
                DetectionRuleType.BRUTE_FORCE
        );

        ThreatDetection detection =
                new ThreatDetection();

        detection.setRule(rule);
        detection.setEvent(event);

        detection.setConfidenceScore(
                BigDecimal.valueOf(100)
        );

        RiskCalculationResult result =
                calculator.calculate(
                        detection,
                        event
                );

        assertEquals(
                100,
                result.riskScore()
        );

        assertEquals(
                RiskSeverity.CRITICAL,
                result.severity()
        );
    }

    @Test
    void shouldClampScoreTo100() {

        SecurityEvent event =
                new SecurityEvent();

        event.setSeverity(
                Severity.CRITICAL
        );

        DetectionRule rule =
                new DetectionRule();

        rule.setRuleType(
                DetectionRuleType.BRUTE_FORCE
        );

        ThreatDetection detection =
                new ThreatDetection();

        detection.setRule(rule);
        detection.setEvent(event);

        detection.setConfidenceScore(
                BigDecimal.valueOf(100)
        );

        RiskCalculationResult result =
                calculator.calculate(
                        detection,
                        event
                );

        assertTrue(
                result.riskScore() <= 100
        );
    }
}