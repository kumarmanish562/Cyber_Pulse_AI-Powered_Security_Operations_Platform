package cyberpulse.notification;

import cyberpulse.incident.event.IncidentCreatedEvent;
import cyberpulse.notification.event.IncidentNotificationListener;
import cyberpulse.notification.service.NotificationService;
import cyberpulse.user.entity.User;
import cyberpulse.user.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncidentNotificationListenerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private IncidentNotificationListener listener;


    @Test
    void shouldCreateNotificationForUsersWithIncidentReadPermission() {

        UUID userId =
                UUID.randomUUID();

        UUID incidentId =
                UUID.randomUUID();

        User user =
                new User();

        user.setId(userId);

        user.setUsername("analyst1");

        user.setEnabled(true);

        user.setLocked(false);

        IncidentCreatedEvent event =
                new IncidentCreatedEvent(
                        incidentId,
                        UUID.randomUUID(),
                        95,
                        "CRITICAL"
                );

        when(
                userRepository
                        .findUsersWithIncidentReadPermission()
        )
                .thenReturn(
                        List.of(user)
                );

        listener.handle(event);

        verify(
                notificationService
        )
                .create(
                        eq(userId),
                        eq("CRITICAL_RISK"),
                        eq("Security incident created"),
                        contains("CRITICAL"),
                        eq("Incident"),
                        eq(incidentId)
                );
    }

    @Test
    void shouldCreateHighRiskNotification() {

        UUID userId =
                UUID.randomUUID();

        UUID incidentId =
                UUID.randomUUID();

        User user =
                new User();

        user.setId(userId);

        user.setUsername("analyst1");

        when(
                userRepository
                        .findUsersWithIncidentReadPermission()
        )
                .thenReturn(
                        List.of(user)
                );

        IncidentCreatedEvent event =
                new IncidentCreatedEvent(
                        incidentId,
                        UUID.randomUUID(),
                        80,
                        "HIGH"
                );

        listener.handle(event);

        verify(
                notificationService
        )
                .create(
                        eq(userId),
                        eq("HIGH_RISK"),
                        anyString(),
                        anyString(),
                        eq("Incident"),
                        eq(incidentId)
                );
    }
}