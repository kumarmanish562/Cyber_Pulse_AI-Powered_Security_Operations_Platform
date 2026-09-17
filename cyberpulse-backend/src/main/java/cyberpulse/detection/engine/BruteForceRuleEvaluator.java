package cyberpulse.detection.engine;


import cyberpulse.common.enums.DetectionRuleType;
import cyberpulse.detection.entity.DetectionRule;
import cyberpulse.event.entity.SecurityEvent;
import cyberpulse.event.repository.SecurityEventRepository;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class BruteForceRuleEvaluator
        implements DetectionRuleEvaluator {

    private final SecurityEventRepository eventRepository;

    public BruteForceRuleEvaluator(
            SecurityEventRepository eventRepository
    ) {
        this.eventRepository = eventRepository;
    }

    @Override
    public boolean supports(
            DetectionRule rule
    ) {

        return rule.getRuleType()
                == DetectionRuleType.BRUTE_FORCE;
    }

    @Override
    public Optional<DetectionEvaluation> evaluate(
            SecurityEvent event,
            DetectionRule rule
    ) {

        if (event.getEventType() !=
                cyberpulse.common.enums.EventType.LOGIN_FAILED) {

            return Optional.empty();
        }

        if (event.getSourceIp() == null) {
            return Optional.empty();
        }

        if (rule.getThresholdValue() == null ||
                rule.getTimeWindowSeconds() == null) {

            return Optional.empty();
        }

        Instant windowStart =
                event.getEventTime()
                        .minusSeconds(
                                rule.getTimeWindowSeconds()
                        );

        long failedAttempts =
                eventRepository.countFailedLoginsFromIp(
                        event.getSourceIp(),
                        windowStart,
                        event.getEventTime()
                );

        if (failedAttempts <
                rule.getThresholdValue()) {

            return Optional.empty();
        }

        BigDecimal confidence =
                calculateConfidence(
                        failedAttempts,
                        rule.getThresholdValue()
                );

        Map<String, Object> details =
                new LinkedHashMap<>();

        details.put(
                "sourceIp",
                event.getSourceIp()
                        .getHostAddress()
        );

        details.put(
                "failedAttempts",
                failedAttempts
        );

        details.put(
                "threshold",
                rule.getThresholdValue()
        );

        details.put(
                "windowSeconds",
                rule.getTimeWindowSeconds()
        );

        details.put(
                "username",
                event.getUsername()
        );

        return Optional.of(
                new DetectionEvaluation(
                        "BRUTE_FORCE",
                        confidence,
                        details
                )
        );
    }

    private BigDecimal calculateConfidence(
            long attempts,
            int threshold
    ) {

        double ratio =
                (double) attempts /
                        threshold;

        double score =
                Math.min(
                        100.0,
                        70.0 + ((ratio - 1.0) * 10.0)
                );

        return BigDecimal.valueOf(
                Math.round(score * 100.0) / 100.0
        );
    }
}