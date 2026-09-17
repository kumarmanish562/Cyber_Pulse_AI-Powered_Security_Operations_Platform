package cyberpulse.notification;

import cyberpulse.notification.dto.NotificationResponse;
import cyberpulse.notification.entity.Notification;
import cyberpulse.notification.mapper.NotificationMapper;
import cyberpulse.user.entity.User;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationMapperTest {

    private final NotificationMapper mapper =
            new NotificationMapper();


    @Test
    void shouldMapNotificationToResponse() {

        UUID notificationId =
                UUID.randomUUID();

        UUID entityId =
                UUID.randomUUID();

        Instant createdAt =
                Instant.parse(
                        "2026-09-17T10:00:00Z"
                );

        User user =
                new User();

        user.setId(
                UUID.randomUUID()
        );

        Notification notification =
                new Notification();

        notification.setId(
                notificationId
        );

        notification.setUser(
                user
        );

        notification.setTitle(
                "Security incident created"
        );

        notification.setMessage(
                "A critical incident requires attention."
        );

        notification.setType(
                "CRITICAL_RISK"
        );

        notification.setEntityType(
                "Incident"
        );

        notification.setEntityId(
                entityId
        );

        notification.setRead(
                false
        );

        notification.setCreatedAt(
                createdAt
        );

        NotificationResponse response =
                mapper.toResponse(
                        notification
                );

        assertThat(response.id())
                .isEqualTo(notificationId);

        assertThat(response.title())
                .isEqualTo(
                        "Security incident created"
                );

        assertThat(response.message())
                .isEqualTo(
                        "A critical incident requires attention."
                );

        assertThat(response.type())
                .isEqualTo(
                        "CRITICAL_RISK"
                );

        assertThat(response.entityType())
                .isEqualTo(
                        "Incident"
                );

        assertThat(response.entityId())
                .isEqualTo(entityId);

        assertThat(response.read())
                .isFalse();

        assertThat(response.createdAt())
                .isEqualTo(createdAt);
    }
}