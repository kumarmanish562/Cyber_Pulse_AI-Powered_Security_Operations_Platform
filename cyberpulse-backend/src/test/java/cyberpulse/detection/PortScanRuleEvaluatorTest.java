package cyberpulse.detection.engine;

import cyberpulse.common.enums.DetectionRuleType;
import cyberpulse.common.enums.EventType;
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
class PortScanRuleEvaluatorTest {

    @Mock
    private SecurityEventRepository eventRepository;

    @InjectMocks
    private PortScanRuleEvaluator evaluator;

    @Test
    void shouldTriggerWhenPortScanCountMeetsThreshold() throws Exception {
        DetectionRule rule = new DetectionRule();
        rule.setRuleType(DetectionRuleType.PORT_SCAN);
        rule.setThresholdValue(5);
        rule.setTimeWindowSeconds(180);

        SecurityEvent event = new SecurityEvent();
        event.setEventType(EventType.PORT_SCAN);
        event.setSourceIp(InetAddress.getByName("192.168.1.99"));
        event.setService("edge-fw");
        event.setEventTime(Instant.now());

        when(eventRepository.countEventsByTypeAndIp(
                eq(EventType.PORT_SCAN),
                eq(event.getSourceIp()),
                any(Instant.class),
                eq(event.getEventTime())
        )).thenReturn(6L);

        Optional<DetectionRuleEvaluator.DetectionEvaluation> result = evaluator.evaluate(event, rule);

        assertTrue(result.isPresent());
        assertEquals("PORT_SCAN", result.get().threatType());
        assertTrue(result.get().confidenceScore().doubleValue() >= 50.0);
    }

    @Test
    void shouldIgnoreNonPortScanEvents() {
        DetectionRule rule = new DetectionRule();
        rule.setRuleType(DetectionRuleType.PORT_SCAN);

        SecurityEvent event = new SecurityEvent();
        event.setEventType(EventType.LOGIN_FAILED);

        assertTrue(evaluator.evaluate(event, rule).isEmpty());
    }
}
