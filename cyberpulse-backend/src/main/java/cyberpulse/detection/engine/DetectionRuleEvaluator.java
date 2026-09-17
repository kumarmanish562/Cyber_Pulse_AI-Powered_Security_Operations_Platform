package cyberpulse.detection.engine;
import cyberpulse.detection.entity.DetectionRule;
import cyberpulse.event.entity.SecurityEvent;

import java.util.Optional;

public interface DetectionRuleEvaluator {

    boolean supports(DetectionRule rule);

    Optional<DetectionEvaluation> evaluate(
            SecurityEvent event,
            DetectionRule rule
    );

    record DetectionEvaluation(
            String threatType,
            java.math.BigDecimal confidenceScore,
            java.util.Map<String, Object> details
    ) {
    }
}