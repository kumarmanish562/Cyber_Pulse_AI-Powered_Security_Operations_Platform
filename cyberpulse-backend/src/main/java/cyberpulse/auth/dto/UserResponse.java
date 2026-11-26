package cyberpulse.auth.dto;

import java.util.List;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String email,
        List<String> roles,
        boolean mfaEnabled
) {
    public UserResponse(UUID id, String username, String email, List<String> roles) {
        this(id, username, email, roles, false);
    }
}
