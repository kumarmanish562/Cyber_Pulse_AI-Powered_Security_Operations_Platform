package cyberpulse.notification.event;

import cyberpulse.incident.event.IncidentStatusChangedEvent;
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
public class IncidentStatusNotificationListener {

    private final NotificationService notificationService;

    private final UserRepository userRepository;


    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handle(
            IncidentStatusChangedEvent event
    ) {

        try {

            userRepository
                    .findUsersWithIncidentReadPermission()
                    .forEach(user -> {

                        String title =
                                "Incident status changed";

                        String message =
                                "Incident %s changed from %s to %s."
                                        .formatted(
                                                event.incidentId(),
                                                event.oldStatus(),
                                                event.newStatus()
                                        );

                        notificationService.create(

                                user.getId(),

                                "INCIDENT_STATUS_CHANGED",

                                title,

                                message,

                                "Incident",

                                event.incidentId()
                        );
                    });

        } catch (Exception exception) {

            log.error(
                    "Failed to notify incident status change {}",
                    event.incidentId(),
                    exception
            );
        }
    }
}