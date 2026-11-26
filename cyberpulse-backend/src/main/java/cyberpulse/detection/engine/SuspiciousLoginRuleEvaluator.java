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
public class SuspiciousLoginRuleEvaluator implements DetectionRuleEvaluator {

    private final SecurityEventRepository eventRepository;

    public SuspiciousLoginRuleEvaluator(SecurityEventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public boolean supports(DetectionRule rule) {
        return rule.getRuleType() == DetectionRuleType.SUSPICIOUS_LOGIN;
    }

    @Override
    public Optional<DetectionEvaluation> evaluate(SecurityEvent event, DetectionRule rule) {
        if (event.getEventType() != EventType.SUSPICIOUS_LOGIN
                && event.getEventType() != EventType.LOGIN_SUCCESS) {
            return Optional.empty();
        }
        if (rule.getThresholdValue() == null || rule.getTimeWindowSeconds() == null) {
            return Optional.empty();
        }

        if (event.getEventType() == EventType.SUSPICIOUS_LOGIN) {
            Map<String, Object> details = new LinkedHashMap<>();
            details.put("username", event.getUsername());
            details.put("sourceIp", event.getSourceIp() != null ? event.getSourceIp().getHostAddress() : null);
            details.put("service", event.getService());
            details.put("reason", "Explicit suspicious login event");
            return Optional.of(new DetectionEvaluation(
                    "SUSPICIOUS_LOGIN",
                    BigDecimal.valueOf(85.00).setScale(2, RoundingMode.HALF_UP),
                    details
            ));
        }

        if (event.getUsername() == null || event.getUsername().isBlank()) {
            return Optional.empty();
        }

        Instant windowStart = event.getEventTime().minusSeconds(rule.getTimeWindowSeconds());
        long distinctIps = eventRepository.countDistinctSourceIpsForUser(
                event.getUsername(),
                windowStart,
                event.getEventTime()
        );

        if (distinctIps < rule.getThresholdValue()) {
            return Optional.empty();
        }

        double ratio = Math.min(1.0, (double) distinctIps / (rule.getThresholdValue() * 2.0));
        BigDecimal confidence = BigDecimal.valueOf(55.0 + ratio * 45.0).setScale(2, RoundingMode.HALF_UP);

        Map<String, Object> details = new LinkedHashMap<>();
        details.put("username", event.getUsername());
        details.put("distinctSourceIps", distinctIps);
        details.put("threshold", rule.getThresholdValue());
        details.put("windowSeconds", rule.getTimeWindowSeconds());
        details.put("sourceIp", event.getSourceIp() != null ? event.getSourceIp().getHostAddress() : null);

        return Optional.of(new DetectionEvaluation("SUSPICIOUS_LOGIN", confidence, details));
    }
}
