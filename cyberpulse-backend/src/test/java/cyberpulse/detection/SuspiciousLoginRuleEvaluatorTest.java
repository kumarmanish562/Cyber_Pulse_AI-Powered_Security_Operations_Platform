package cyberpulse.detection;

import cyberpulse.common.enums.DetectionRuleType;
import cyberpulse.common.enums.EventType;
import cyberpulse.detection.engine.DetectionRuleEvaluator;
import cyberpulse.detection.engine.SuspiciousLoginRuleEvaluator;
import cyberpulse.detection.entity.DetectionRule;
import cyberpulse.event.entity.SecurityEvent;
import cyberpulse.event.repository.SecurityEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.InetAddress;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SuspiciousLoginRuleEvaluatorTest {

    @Mock
    private SecurityEventRepository eventRepository;

    @InjectMocks
    private SuspiciousLoginRuleEvaluator evaluator;

    @Test
    void shouldTriggerOnMultiIpLoginSuccess() throws Exception {
        DetectionRule rule = new DetectionRule();
        rule.setRuleType(DetectionRuleType.SUSPICIOUS_LOGIN);
        rule.setThresholdValue(2);
        rule.setTimeWindowSeconds(600);

        SecurityEvent event = new SecurityEvent();
        event.setEventType(EventType.LOGIN_SUCCESS);
        event.setUsername("alice");
        event.setSourceIp(InetAddress.getByName("203.0.113.5"));
        event.setEventTime(Instant.now());

        when(eventRepository.countDistinctSourceIpsForUser(
                eq("alice"),
                any(Instant.class),
                eq(event.getEventTime())
        )).thenReturn(3L);

        Optional<DetectionRuleEvaluator.DetectionEvaluation> result = evaluator.evaluate(event, rule);

        assertTrue(result.isPresent());
        assertEquals("SUSPICIOUS_LOGIN", result.get().threatType());
    }

    @Test
    void shouldTriggerDirectlyOnSuspiciousLoginEvent() {
        DetectionRule rule = new DetectionRule();
        rule.setRuleType(DetectionRuleType.SUSPICIOUS_LOGIN);
        rule.setThresholdValue(2);
        rule.setTimeWindowSeconds(600);

        SecurityEvent event = new SecurityEvent();
        event.setEventType(EventType.SUSPICIOUS_LOGIN);
        event.setUsername("bob");

        Optional<DetectionRuleEvaluator.DetectionEvaluation> result = evaluator.evaluate(event, rule);

        assertTrue(result.isPresent());
        assertEquals("SUSPICIOUS_LOGIN", result.get().threatType());
    }
}
