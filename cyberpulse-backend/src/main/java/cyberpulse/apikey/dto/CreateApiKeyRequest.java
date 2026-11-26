package cyberpulse.apikey.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record CreateApiKeyRequest(
        @NotBlank(message = "Key name is required")
        @Size(max = 100, message = "Key name must not exceed 100 characters")
        String name,

        @Size(max = 255, message = "Scopes must not exceed 255 characters")
        String scopes,

        Instant expiresAt
) {
}
