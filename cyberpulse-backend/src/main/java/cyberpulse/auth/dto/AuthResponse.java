package cyberpulse.auth.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn,
        UserResponse user,
        boolean mfaRequired
) {
    public AuthResponse(
            String accessToken,
            String refreshToken,
            String tokenType,
            long expiresIn,
            UserResponse user
    ) {
        this(accessToken, refreshToken, tokenType, expiresIn, user, false);
    }

    public static AuthResponse mfaChallenge(UserResponse user) {
        return new AuthResponse(null, null, "Bearer", 0L, user, true);
    }
}
