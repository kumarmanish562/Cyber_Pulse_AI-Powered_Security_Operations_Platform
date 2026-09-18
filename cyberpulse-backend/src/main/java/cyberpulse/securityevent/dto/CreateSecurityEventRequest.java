package cyberpulse.securityevent.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateSecurityEventRequest(

        @NotBlank(message = "Event type is required")
        String eventType,

        @NotBlank(message = "Source IP is required")
        String sourceIp
) {
}