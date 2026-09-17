package cyberpulse.detection.engine;

import cyberpulse.detection.entity.DetectionRule;
import cyberpulse.detection.repository.DetectionRuleRepository;
import cyberpulse.detection.service.ThreatDetectionService;
import cyberpulse.event.entity.SecurityEvent;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DetectionEngine {

    private final DetectionRuleRepository ruleRepository;

    private final List<DetectionRuleEvaluator> evaluators;

    private final ThreatDetectionService threatDetectionService;

    public DetectionEngine(
            DetectionRuleRepository ruleRepository,
            List<DetectionRuleEvaluator> evaluators,
            ThreatDetectionService threatDetectionService
    ) {
        this.ruleRepository = ruleRepository;
        this.evaluators = evaluators;
        this.threatDetectionService = threatDetectionService;
    }

    public void analyze(SecurityEvent event) {

        List<DetectionRule> rules =
                ruleRepository.findByEnabledTrue();

        for (DetectionRule rule : rules) {

            evaluators.stream()
                    .filter(evaluator ->
                            evaluator.supports(rule)
                    )
                    .findFirst()
                    .ifPresent(evaluator -> {

                        evaluator
                                .evaluate(event, rule)
                                .ifPresent(evaluation ->
                                        threatDetectionService
                                                .createIfNotExists(
                                                        event,
                                                        rule,
                                                        evaluation
                                                )
                                );
                    });
        }
    }
}