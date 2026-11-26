package cyberpulse.detection.engine;

import cyberpulse.common.enums.DetectionRuleType;
import cyberpulse.common.enums.EventType;
import cyberpulse.detection.entity.DetectionRule;
import cyberpulse.event.entity.SecurityEvent;
import cyberpulse.event.repository.SecurityEventRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class PortScanRuleEvaluator implements DetectionRuleEvaluator {

    private final SecurityEventRepository eventRepository;

    public PortScanRuleEvaluator(SecurityEventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public boolean supports(DetectionRule rule) {
        return rule.getRuleType() == DetectionRuleType.PORT_SCAN;
    }

    @Override
    public Optional<DetectionEvaluation> evaluate(SecurityEvent event, DetectionRule rule) {
        if (event.getEventType() != EventType.PORT_SCAN || event.getSourceIp() == null) {
            return Optional.empty();
        }
        if (rule.getThresholdValue() == null || rule.getTimeWindowSeconds() == null) {
            return Optional.empty();
        }

        Instant windowStart = event.getEventTime().minusSeconds(rule.getTimeWindowSeconds());
        long scanCount = eventRepository.countEventsByTypeAndIp(
                EventType.PORT_SCAN,
                event.getSourceIp(),
                windowStart,
                event.getEventTime()
        );

        if (scanCount < rule.getThresholdValue()) {
            return Optional.empty();
        }

        double ratio = Math.min(1.0, (double) scanCount / (rule.getThresholdValue() * 2.0));
        BigDecimal confidence = BigDecimal.valueOf(50.0 + ratio * 50.0).setScale(2, RoundingMode.HALF_UP);

        Map<String, Object> details = new LinkedHashMap<>();
        details.put("sourceIp", event.getSourceIp().getHostAddress());
        details.put("scanEvents", scanCount);
        details.put("threshold", rule.getThresholdValue());
        details.put("windowSeconds", rule.getTimeWindowSeconds());
        details.put("service", event.getService());

        return Optional.of(new DetectionEvaluation("PORT_SCAN", confidence, details));
    }
}
