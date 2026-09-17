package cyberpulse.incident.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AssignIncidentRequest(

        @NotNull(
                message = "User ID is required"
        )
        UUID userId
) {
}