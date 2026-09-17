package cyberpulse.detection.dto;

import cyberpulse.common.enums.DetectionRuleType;
import cyberpulse.common.enums.Severity;
import jakarta.validation.constraints.*;

public record CreateDetectionRuleRequest(

        @NotBlank(message = "Rule name is required")
        @Size(
                max = 100,
                message = "Rule name must not exceed 100 characters"
        )
        String name,

        @NotNull(message = "Rule type is required")
        DetectionRuleType ruleType,

        @Size(
                max = 5000,
                message = "Description must not exceed 5000 characters"
        )
        String description,

        @NotNull(message = "Severity is required")
        Severity severity,

        @Min(
                value = 1,
                message = "Threshold must be greater than zero"
        )
        Integer thresholdValue,

        @Min(
                value = 1,
                message = "Time window must be greater than zero"
        )
        Integer timeWindowSeconds,

        Boolean enabled
) {
}