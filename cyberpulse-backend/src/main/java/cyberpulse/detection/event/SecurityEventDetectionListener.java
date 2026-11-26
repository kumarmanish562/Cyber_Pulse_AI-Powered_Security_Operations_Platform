package cyberpulse.detection.event;

import cyberpulse.detection.engine.DetectionEngine;
import cyberpulse.event.event.SecurityEventCreatedEvent;
import cyberpulse.event.repository.SecurityEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Runs threat detection asynchronously after the ingested SecurityEvent transaction commits,
 * keeping event ingestion fast and avoiding dirty-read race conditions.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityEventDetectionListener {

    private final SecurityEventRepository eventRepository;
    private final DetectionEngine detectionEngine;

    @Async("detectionTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onSecurityEventCreated(SecurityEventCreatedEvent domainEvent) {
        eventRepository.findById(domainEvent.eventId()).ifPresentOrElse(
                detectionEngine::analyze,
                () -> log.warn("Skipping detection; security event not found id={}", domainEvent.eventId())
        );
    }
}
