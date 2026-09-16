package cyberpulse.event.dto;

import cyberpulse.common.enums.EventType;
import cyberpulse.common.enums.Severity;

import java.time.Instant;
import java.util.UUID;

public record SecurityEventResponse(

        UUID id,

        EventType eventType,

        String sourceIp,

        String username,

        String service,

        String message,

        Severity severity,

        Instant eventTime,

        Instant createdAt
) {
}