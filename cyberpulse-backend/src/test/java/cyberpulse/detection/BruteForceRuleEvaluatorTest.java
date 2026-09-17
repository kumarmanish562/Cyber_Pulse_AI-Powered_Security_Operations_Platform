package cyberpulse.detection;

import cyberpulse.common.enums.DetectionRuleType;
import cyberpulse.common.enums.EventType;
import cyberpulse.common.enums.Severity;
import cyberpulse.detection.engine.BruteForceRuleEvaluator;
import cyberpulse.detection.engine.DetectionRuleEvaluator;
import cyberpulse.detection.entity.DetectionRule;
import cyberpulse.event.entity.SecurityEvent;
import cyberpulse.event.repository.SecurityEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.InetAddress;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BruteForceRuleEvaluatorTest {

    @Mock
    private SecurityEventRepository eventRepository;

    @Test
    void shouldDetectBruteForce() throws Exception {

        BruteForceRuleEvaluator evaluator =
                new BruteForceRuleEvaluator(
                        eventRepository
                );

        DetectionRule rule =
                new DetectionRule();

        rule.setRuleType(
                DetectionRuleType.BRUTE_FORCE
        );

        rule.setThresholdValue(10);
        rule.setTimeWindowSeconds(120);
        rule.setSeverity(Severity.HIGH);

        SecurityEvent event =
                new SecurityEvent();

        event.setEventType(
                EventType.LOGIN_FAILED
        );

        event.setSourceIp(
                InetAddress.getByName(
                        "192.168.1.50"
                )
        );

        event.setEventTime(
                Instant.parse(
                        "2026-09-16T16:30:00Z"
                )
        );

        when(
                eventRepository.countFailedLoginsFromIp(
                        event.getSourceIp(),
                        Instant.parse(
                                "2026-09-16T16:28:00Z"
                        ),
                        event.getEventTime()
                )
        ).thenReturn(10L);

        Optional<DetectionRuleEvaluator.DetectionEvaluation>
                result =
                evaluator.evaluate(
                        event,
                        rule
                );

        assertThat(result)
                .isPresent();

        assertThat(
                result.get().threatType()
        )
                .isEqualTo("BRUTE_FORCE");

        assertThat(
                result.get().details()
                        .get("failedAttempts")
        )
                .isEqualTo(10L);
    }

    @Test
    void shouldNotDetectBelowThreshold()
            throws Exception {

        BruteForceRuleEvaluator evaluator =
                new BruteForceRuleEvaluator(
                        eventRepository
                );

        DetectionRule rule =
                new DetectionRule();

        rule.setRuleType(
                DetectionRuleType.BRUTE_FORCE
        );

        rule.setThresholdValue(10);
        rule.setTimeWindowSeconds(120);

        SecurityEvent event =
                new SecurityEvent();

        event.setEventType(
                EventType.LOGIN_FAILED
        );

        event.setSourceIp(
                InetAddress.getByName(
                        "192.168.1.50"
                )
        );

        event.setEventTime(
                Instant.parse(
                        "2026-09-16T16:30:00Z"
                )
        );

        when(
                eventRepository.countFailedLoginsFromIp(
                        event.getSourceIp(),
                        Instant.parse(
                                "2026-09-16T16:28:00Z"
                        ),
                        event.getEventTime()
                )
        ).thenReturn(9L);

        Optional<DetectionRuleEvaluator.DetectionEvaluation>
                result =
                evaluator.evaluate(
                        event,
                        rule
                );

        assertThat(result)
                .isEmpty();
    }
}