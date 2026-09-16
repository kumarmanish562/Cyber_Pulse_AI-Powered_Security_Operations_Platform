package cyberpulse.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AssignRoleRequest(

        @NotBlank(message = "Role is required")

        @Pattern(
                regexp =
                        "ADMIN|SECURITY_ANALYST|SECURITY_ENGINEER|VIEWER",
                message = "Invalid role"
        )

        String role
) {
}