package cyberpulse.incident.dto;

import cyberpulse.incident.entity.IncidentStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateIncidentStatusRequest(

        @NotNull(message = "Status is required")
        IncidentStatus status

) {
}