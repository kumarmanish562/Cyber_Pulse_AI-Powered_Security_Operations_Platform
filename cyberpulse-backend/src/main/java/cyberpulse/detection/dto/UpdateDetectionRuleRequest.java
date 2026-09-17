package cyberpulse.detection.dto;

import cyberpulse.common.enums.Severity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record UpdateDetectionRuleRequest(

        @Size(
                max = 5000,
                message = "Description must not exceed 5000 characters"
        )
        String description,

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