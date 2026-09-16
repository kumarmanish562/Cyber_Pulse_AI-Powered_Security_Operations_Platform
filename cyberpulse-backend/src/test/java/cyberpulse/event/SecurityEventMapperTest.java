package cyberpulse.event;

import cyberpulse.common.enums.EventType;
import cyberpulse.common.enums.Severity;
import cyberpulse.event.dto.CreateSecurityEventRequest;
import cyberpulse.event.dto.SecurityEventResponse;
import cyberpulse.event.entity.SecurityEvent;
import cyberpulse.event.mapper.SecurityEventMapper;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityEventMapperTest {

    private final SecurityEventMapper mapper =
            new SecurityEventMapper();

    @Test
    void shouldMapRequestToEntity() {

        CreateSecurityEventRequest request =
                new CreateSecurityEventRequest(
                        EventType.LOGIN_FAILED,
                        "192.168.1.50",
                        "admin",
                        "auth-service",
                        "Failed login",
                        Severity.MEDIUM,
                        Instant.parse(
                                "2026-09-16T16:30:00Z"
                        )
                );

        SecurityEvent event =
                mapper.toEntity(request);

        assertThat(event.getEventType())
                .isEqualTo(EventType.LOGIN_FAILED);

        assertThat(event.getSourceIp().getHostAddress())
                .isEqualTo("192.168.1.50");

        assertThat(event.getUsername())
                .isEqualTo("admin");

        assertThat(event.getService())
                .isEqualTo("auth-service");

        assertThat(event.getSeverity())
                .isEqualTo(Severity.MEDIUM);
    }

    @Test
    void shouldMapEntityToResponse() {

        SecurityEvent event =
                new SecurityEvent();

        event.setEventType(
                EventType.LOGIN_FAILED
        );

        event.setSeverity(
                Severity.MEDIUM
        );

        SecurityEventResponse response =
                mapper.toResponse(event);

        assertThat(response.eventType())
                .isEqualTo(EventType.LOGIN_FAILED);

        assertThat(response.severity())
                .isEqualTo(Severity.MEDIUM);
    }
}