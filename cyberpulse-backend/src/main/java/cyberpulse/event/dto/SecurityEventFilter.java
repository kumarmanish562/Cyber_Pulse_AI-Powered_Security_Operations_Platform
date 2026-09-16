package cyberpulse.event.dto;

import cyberpulse.common.enums.EventType;
import cyberpulse.common.enums.Severity;

import java.time.Instant;

public record SecurityEventFilter(

        EventType eventType,

        Severity severity,

        String sourceIp,

        String username,

        String service,

        Instant from,

        Instant to
) {
}