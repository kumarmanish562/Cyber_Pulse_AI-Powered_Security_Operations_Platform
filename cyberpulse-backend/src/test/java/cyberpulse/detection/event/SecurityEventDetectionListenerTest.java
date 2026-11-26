package cyberpulse.detection.event;

import cyberpulse.detection.engine.DetectionEngine;
import cyberpulse.event.entity.SecurityEvent;
import cyberpulse.event.event.SecurityEventCreatedEvent;
import cyberpulse.event.repository.SecurityEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityEventDetectionListenerTest {

    @Mock
    private SecurityEventRepository eventRepository;

    @Mock
    private DetectionEngine detectionEngine;

    @InjectMocks
    private SecurityEventDetectionListener listener;

    @Test
    void shouldAnalyzeEventWhenFound() {
        UUID id = UUID.randomUUID();
        SecurityEvent event = new SecurityEvent();
        event.setId(id);

        when(eventRepository.findById(id)).thenReturn(Optional.of(event));

        listener.onSecurityEventCreated(new SecurityEventCreatedEvent(id));

        verify(detectionEngine).analyze(event);
    }

    @Test
    void shouldSkipWhenEventNotFound() {
        UUID id = UUID.randomUUID();
        when(eventRepository.findById(id)).thenReturn(Optional.empty());

        listener.onSecurityEventCreated(new SecurityEventCreatedEvent(id));

        verifyNoInteractions(detectionEngine);
    }
}
