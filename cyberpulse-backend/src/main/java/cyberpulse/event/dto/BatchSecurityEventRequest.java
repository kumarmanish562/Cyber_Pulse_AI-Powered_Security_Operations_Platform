package cyberpulse.event.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record BatchSecurityEventRequest(
        @NotEmpty(message = "Events list must not be empty")
        @Size(max = 500, message = "Maximum 500 events allowed per batch")
        @Valid
        List<CreateSecurityEventRequest> events
) {
}
