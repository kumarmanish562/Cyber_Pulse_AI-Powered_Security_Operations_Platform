package cyberpulse.detection.dto;

import cyberpulse.common.enums.DetectionRuleType;
import cyberpulse.common.enums.Severity;

import java.time.Instant;
import java.util.UUID;

public record DetectionRuleResponse(

        UUID id,

        String name,

        DetectionRuleType ruleType,

        String description,

        Severity severity,

        Integer thresholdValue,

        Integer timeWindowSeconds,

        boolean enabled,

        UUID createdBy,

        Instant createdAt,

        Instant updatedAt
) {
}