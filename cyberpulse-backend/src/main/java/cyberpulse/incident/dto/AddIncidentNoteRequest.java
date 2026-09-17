package cyberpulse.incident.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddIncidentNoteRequest(

        @NotBlank(message = "Note content is required")
        @Size(
                max = 5000,
                message = "Note cannot exceed 5000 characters"
        )
        String content

) {
}