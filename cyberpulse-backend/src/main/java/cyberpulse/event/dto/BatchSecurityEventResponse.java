package cyberpulse.event.dto;

import java.util.List;

public record BatchSecurityEventResponse(
        int ingestedCount,
        List<SecurityEventResponse> events
) {
}
