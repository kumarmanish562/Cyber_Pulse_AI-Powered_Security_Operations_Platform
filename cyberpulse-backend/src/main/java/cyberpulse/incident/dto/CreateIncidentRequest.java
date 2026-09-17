package cyberpulse.incident.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateIncidentRequest(

        @NotNull(
                message = "Risk assessment ID is required"
        )
        UUID riskAssessmentId,

        @NotBlank(
                message = "Title is required"
        )
        @Size(
                min = 5,
                max = 200
        )
        String title,

        @NotBlank(
                message = "Description is required"
        )
        @Size(
                min = 10,
                max = 5000
        )
        String description
) {
}