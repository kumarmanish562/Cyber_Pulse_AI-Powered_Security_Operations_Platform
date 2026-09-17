package cyberpulse.common.response;

import java.time.Instant;
import java.util.List;

public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String code,
        String message,
        String path,
        String correlationId,
        List<FieldErrorResponse> errors
) {

    public record FieldErrorResponse(
            String field,
            String message
    ) {
    }
}