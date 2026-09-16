package cyberpulse.event;

import cyberpulse.common.enums.EventType;
import cyberpulse.common.enums.Severity;
import cyberpulse.event.dto.CreateSecurityEventRequest;
import cyberpulse.event.dto.SecurityEventResponse;
import cyberpulse.event.entity.SecurityEvent;
import cyberpulse.event.mapper.SecurityEventMapper;
import cyberpulse.event.repository.SecurityEventRepository;
import cyberpulse.event.service.SecurityEventService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityEventServiceTest {

    @Mock
    private SecurityEventRepository eventRepository;

    @Mock
    private SecurityEventMapper eventMapper;

    @InjectMocks
    private SecurityEventService eventService;

    @Test
    void shouldCreateSecurityEvent() {

        CreateSecurityEventRequest request =
                new CreateSecurityEventRequest(
                        EventType.LOGIN_FAILED,
                        "192.168.1.50",
                        "admin",
                        "auth-service",
                        "Failed login",
                        Severity.MEDIUM,
                        Instant.now()
                );

        SecurityEvent entity =
                new SecurityEvent();

        SecurityEvent saved =
                new SecurityEvent();

        SecurityEventResponse response =
                new SecurityEventResponse(
                        null,
                        EventType.LOGIN_FAILED,
                        "192.168.1.50",
                        "admin",
                        "auth-service",
                        "Failed login",
                        Severity.MEDIUM,
                        request.eventTime(),
                        Instant.now()
                );

        when(eventMapper.toEntity(request))
                .thenReturn(entity);

        when(eventRepository.save(entity))
                .thenReturn(saved);

        when(eventMapper.toResponse(saved))
                .thenReturn(response);

        SecurityEventResponse result =
                eventService.createEvent(request);

        assertThat(result.eventType())
                .isEqualTo(EventType.LOGIN_FAILED);

        assertThat(result.severity())
                .isEqualTo(Severity.MEDIUM);
    }
}