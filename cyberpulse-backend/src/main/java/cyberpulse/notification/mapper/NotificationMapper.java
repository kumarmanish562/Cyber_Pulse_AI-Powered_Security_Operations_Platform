package cyberpulse.notification.mapper;

import cyberpulse.notification.dto.NotificationResponse;
import cyberpulse.notification.entity.Notification;

import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(
            Notification notification
    ) {

        return new NotificationResponse(

                notification.getId(),

                notification.getTitle(),

                notification.getMessage(),

                notification.getType(),

                notification.getEntityType(),

                notification.getEntityId(),

                notification.isRead(),

                notification.getCreatedAt()

        );
    }
}