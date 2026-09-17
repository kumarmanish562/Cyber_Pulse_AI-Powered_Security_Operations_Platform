package cyberpulse.detection.mapper;

import cyberpulse.detection.dto.CreateDetectionRuleRequest;
import cyberpulse.detection.dto.DetectionRuleResponse;
import cyberpulse.detection.dto.UpdateDetectionRuleRequest;
import cyberpulse.detection.entity.DetectionRule;
import org.springframework.stereotype.Component;

@Component
public class DetectionRuleMapper {

    public DetectionRule toEntity(
            CreateDetectionRuleRequest request
    ) {

        DetectionRule rule = new DetectionRule();

        rule.setName(
                request.name().trim()
        );

        rule.setRuleType(
                request.ruleType()
        );

        rule.setDescription(
                normalize(request.description())
        );

        rule.setSeverity(
                request.severity()
        );

        rule.setThresholdValue(
                request.thresholdValue()
        );

        rule.setTimeWindowSeconds(
                request.timeWindowSeconds()
        );

        rule.setEnabled(
                request.enabled() == null
                        || request.enabled()
        );

        return rule;
    }

    public void updateEntity(
            DetectionRule rule,
            UpdateDetectionRuleRequest request
    ) {

        if (request.description() != null) {
            rule.setDescription(
                    normalize(request.description())
            );
        }

        if (request.severity() != null) {
            rule.setSeverity(
                    request.severity()
            );
        }

        if (request.thresholdValue() != null) {
            rule.setThresholdValue(
                    request.thresholdValue()
            );
        }

        if (request.timeWindowSeconds() != null) {
            rule.setTimeWindowSeconds(
                    request.timeWindowSeconds()
            );
        }

        if (request.enabled() != null) {
            rule.setEnabled(
                    request.enabled()
            );
        }
    }

    public DetectionRuleResponse toResponse(
            DetectionRule rule
    ) {

        return new DetectionRuleResponse(
                rule.getId(),
                rule.getName(),
                rule.getRuleType(),
                rule.getDescription(),
                rule.getSeverity(),
                rule.getThresholdValue(),
                rule.getTimeWindowSeconds(),
                rule.isEnabled(),
                rule.getCreatedBy() != null
                        ? rule.getCreatedBy().getId()
                        : null,
                rule.getCreatedAt(),
                rule.getUpdatedAt()
        );
    }

    private String normalize(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}