package cyberpulse.notification.event;

import cyberpulse.incident.event.IncidentCreatedEvent;
import cyberpulse.notification.service.NotificationService;
import cyberpulse.user.entity.User;
import cyberpulse.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class IncidentNotificationListener {

    private final NotificationService notificationService;

    private final UserRepository userRepository;


    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handle(
            IncidentCreatedEvent event
    ) {

        try {

            userRepository
                    .findUsersWithIncidentReadPermission()
                    .forEach(
                            user ->
                                    createNotification(
                                            user,
                                            event
                                    )
                    );

        } catch (Exception exception) {

            log.error(
                    "Failed to create notifications for incident {}",
                    event.incidentId(),
                    exception
            );
        }
    }


    private void createNotification(
            User user,
            IncidentCreatedEvent event
    ) {

        String type =
                determineType(
                        event.severity()
                );

        String title =
                "Security incident created";

        String message =
                "A %s security incident requires attention. Risk score: %d"
                        .formatted(
                                event.severity(),
                                event.riskScore()
                        );

        notificationService.create(

                user.getId(),

                type,

                title,

                message,

                "Incident",

                event.incidentId()
        );
    }


    private String determineType(
            String severity
    ) {

        if (severity == null) {
            return "INCIDENT_CREATED";
        }

        return switch (
                severity.toUpperCase()
                ) {

            case "CRITICAL" ->
                    "CRITICAL_RISK";

            case "HIGH" ->
                    "HIGH_RISK";

            default ->
                    "INCIDENT_CREATED";
        };
    }
}