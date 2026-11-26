package cyberpulse.blocklist.dto;

import cyberpulse.common.enums.Severity;
import cyberpulse.common.validation.IpAddress;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record BlockIpRequest(
        @NotBlank(message = "IP address is required")
        @IpAddress
        String ipAddress,

        @NotBlank(message = "Reason is required")
        @Size(max = 1000, message = "Reason must not exceed 1000 characters")
        String reason,

        Severity severity,

        Instant expiresAt
) {
}
