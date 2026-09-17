package cyberpulse.detection;

import cyberpulse.detection.engine.DetectionEngine;
import cyberpulse.detection.engine.DetectionRuleEvaluator;
import cyberpulse.detection.entity.DetectionRule;
import cyberpulse.detection.repository.DetectionRuleRepository;
import cyberpulse.detection.service.ThreatDetectionService;
import cyberpulse.event.entity.SecurityEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DetectionEngineTest {

    @Mock
    private DetectionRuleRepository ruleRepository;

    @Mock
    private DetectionRuleEvaluator evaluator;

    @Mock
    private ThreatDetectionService
            threatDetectionService;

    @Test
    void shouldEvaluateEnabledRules() {

        DetectionRule rule =
                new DetectionRule();

        SecurityEvent event =
                new SecurityEvent();

        DetectionRuleEvaluator.DetectionEvaluation
                evaluation =
                new DetectionRuleEvaluator.DetectionEvaluation(
                        "BRUTE_FORCE",
                        java.math.BigDecimal.valueOf(90),
                        java.util.Map.of(
                                "failedAttempts",
                                10
                        )
                );

        when(
                ruleRepository.findByEnabledTrue()
        ).thenReturn(
                List.of(rule)
        );

        when(
                evaluator.supports(rule)
        ).thenReturn(true);

        when(
                evaluator.evaluate(
                        event,
                        rule
                )
        ).thenReturn(
                Optional.of(evaluation)
        );

        DetectionEngine engine =
                new DetectionEngine(
                        ruleRepository,
                        List.of(evaluator),
                        threatDetectionService
                );

        engine.analyze(event);

        verify(evaluator)
                .evaluate(
                        event,
                        rule
                );

        verify(threatDetectionService)
                .createIfNotExists(
                        event,
                        rule,
                        evaluation
                );
    }
}