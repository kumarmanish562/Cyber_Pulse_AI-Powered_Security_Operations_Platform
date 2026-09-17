package cyberpulse.event;

import cyberpulse.common.enums.EventType;
import cyberpulse.common.enums.Severity;
import cyberpulse.detection.engine.DetectionEngine;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityEventServiceTest {

    @Mock
    private SecurityEventRepository eventRepository;

    @Mock
    private SecurityEventMapper eventMapper;

    @Mock
    private DetectionEngine detectionEngine;

    @InjectMocks
    private SecurityEventService eventService;


    @Test
    void shouldCreateSecurityEvent() {

        // ============================================================
        // 1. Create request
        // ============================================================

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


        // ============================================================
        // 2. Mock entity conversion
        // ============================================================

        SecurityEvent entity =
                new SecurityEvent();


        // ============================================================
        // 3. Mock saved entity
        // ============================================================

        SecurityEvent saved =
                new SecurityEvent();


        // ============================================================
        // 4. Expected response
        // ============================================================

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


        // ============================================================
        // 5. Mock mapper
        // ============================================================

        when(eventMapper.toEntity(request))
                .thenReturn(entity);


        // ============================================================
        // 6. Mock repository
        // ============================================================

        when(eventRepository.save(entity))
                .thenReturn(saved);


        // ============================================================
        // 7. Mock response mapping
        // ============================================================

        when(eventMapper.toResponse(saved))
                .thenReturn(response);


        // ============================================================
        // 8. Execute service
        // ============================================================

        SecurityEventResponse result =
                eventService.createEvent(request);


        // ============================================================
        // 9. Verify result
        // ============================================================

        assertThat(result.eventType())
                .isEqualTo(EventType.LOGIN_FAILED);

        assertThat(result.severity())
                .isEqualTo(Severity.MEDIUM);
    }
}