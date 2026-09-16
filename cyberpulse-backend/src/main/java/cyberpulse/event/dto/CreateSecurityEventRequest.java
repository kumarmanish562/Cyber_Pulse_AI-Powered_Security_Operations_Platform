package cyberpulse.event.dto;

import cyberpulse.common.enums.EventType;
import cyberpulse.common.enums.Severity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.IpAddress;

import java.time.Instant;

public record CreateSecurityEventRequest(

        @NotNull(message = "Event type is required")
        EventType eventType,

        @IpAddress
        String sourceIp,

        @Size(max = 100, message = "Username must not exceed 100 characters")
        String username,

        @Size(max = 100, message = "Service must not exceed 100 characters")
        String service,

        @Size(max = 5000, message = "Message must not exceed 5000 characters")
        String message,

        @NotNull(message = "Severity is required")
        Severity severity,

        @NotNull(message = "Event time is required")
        Instant eventTime
) {
}
