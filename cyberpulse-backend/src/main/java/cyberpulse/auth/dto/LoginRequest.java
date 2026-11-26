package cyberpulse.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Username or email is required")
        String usernameOrEmail,

        @NotBlank(message = "Password is required")
        String password,

        String totpCode
) {
    public LoginRequest(String usernameOrEmail, String password) {
        this(usernameOrEmail, password, null);
    }
}
