package cyberpulse.notification.repository;

import cyberpulse.notification.entity.Notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface NotificationRepository
        extends JpaRepository<Notification, UUID> {

    Page<Notification>
    findByUser_IdOrderByCreatedAtDesc(
            UUID userId,
            Pageable pageable
    );

    Page<Notification>
    findByUser_IdAndReadFalseOrderByCreatedAtDesc(
            UUID userId,
            Pageable pageable
    );

    long countByUser_IdAndReadFalse(
            UUID userId
    );

    @Modifying
    @Query("""
        UPDATE Notification n
        SET n.read = true
        WHERE n.id = :notificationId
          AND n.user.id = :userId
    """)
    int markAsRead(
            @Param("notificationId")
            UUID notificationId,

            @Param("userId")
            UUID userId
    );

    @Modifying
    @Query("""
        UPDATE Notification n
        SET n.read = true
        WHERE n.user.id = :userId
          AND n.read = false
    """)
    int markAllAsRead(
            @Param("userId")
            UUID userId
    );
}