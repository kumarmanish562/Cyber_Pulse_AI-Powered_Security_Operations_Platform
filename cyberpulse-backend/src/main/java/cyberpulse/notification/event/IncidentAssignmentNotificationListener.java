package cyberpulse.notification.event;

import cyberpulse.incident.event.IncidentAssignedEvent;
import cyberpulse.notification.service.NotificationService;
import cyberpulse.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class IncidentAssignmentNotificationListener {

    private final NotificationService notificationService;

    private final UserRepository userRepository;


    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handle(
            IncidentAssignedEvent event
    ) {

        if (event.newAssignee() == null) {
            return;
        }

        try {

            userRepository
                    .findById(
                            event.newAssignee()
                    )
                    .filter(user ->
                            user.isEnabled()
                                    && !user.isLocked()
                    )
                    .ifPresent(user -> {

                        notificationService.create(

                                user.getId(),

                                "INCIDENT_ASSIGNED",

                                "Incident assigned to you",

                                "Incident %s has been assigned to you."
                                        .formatted(
                                                event.incidentId()
                                        ),

                                "Incident",

                                event.incidentId()
                        );
                    });

        } catch (Exception exception) {

            log.error(
                    "Failed to notify incident assignment {}",
                    event.incidentId(),
                    exception
            );
        }
    }
}