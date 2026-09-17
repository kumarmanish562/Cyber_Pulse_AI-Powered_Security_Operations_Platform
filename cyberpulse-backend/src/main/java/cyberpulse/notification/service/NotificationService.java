package cyberpulse.notification.service;

import cyberpulse.notification.dto.NotificationResponse;
import cyberpulse.notification.dto.UnreadCountResponse;
import cyberpulse.notification.entity.Notification;
import cyberpulse.notification.exception.NotificationNotFoundException;
import cyberpulse.notification.mapper.NotificationMapper;
import cyberpulse.notification.repository.NotificationRepository;
import cyberpulse.user.entity.User;
import cyberpulse.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    private final NotificationMapper notificationMapper;

    private final UserRepository userRepository;


    // =========================================================
    // CURRENT USER
    // =========================================================

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(
                authentication.getPrincipal()
        )) {

            throw new IllegalStateException(
                    "Authenticated user not found"
            );
        }

        String username =
                authentication.getName();

        return userRepository
                .findByUsernameIgnoreCase(username)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Authenticated user not found: "
                                        + username
                        )
                );
    }


    // =========================================================
    // CREATE NOTIFICATION
    // =========================================================

    @Transactional
    public Notification create(
            UUID userId,
            String type,
            String title,
            String message,
            String entityType,
            UUID entityId
    ) {

        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "User not found: "
                                                + userId
                                )
                        );

        Notification notification =
                new Notification();

        notification.setUser(user);

        notification.setType(type);

        notification.setTitle(title);

        notification.setMessage(message);

        notification.setEntityType(entityType);

        notification.setEntityId(entityId);

        notification.setRead(false);

        return notificationRepository.save(
                notification
        );
    }


    // =========================================================
    // MY NOTIFICATIONS
    // =========================================================

    @Transactional(readOnly = true)
    public Page<NotificationResponse>
    getMyNotifications(
            Pageable pageable
    ) {

        UUID userId =
                getCurrentUser().getId();

        return notificationRepository
                .findByUser_IdOrderByCreatedAtDesc(
                        userId,
                        pageable
                )
                .map(notificationMapper::toResponse);
    }


    // =========================================================
    // MY UNREAD NOTIFICATIONS
    // =========================================================

    @Transactional(readOnly = true)
    public Page<NotificationResponse>
    getMyUnreadNotifications(
            Pageable pageable
    ) {

        UUID userId =
                getCurrentUser().getId();

        return notificationRepository
                .findByUser_IdAndReadFalseOrderByCreatedAtDesc(
                        userId,
                        pageable
                )
                .map(notificationMapper::toResponse);
    }


    // =========================================================
    // UNREAD COUNT
    // =========================================================

    @Transactional(readOnly = true)
    public UnreadCountResponse getUnreadCount() {

        UUID userId =
                getCurrentUser().getId();

        long count =
                notificationRepository
                        .countByUser_IdAndReadFalse(
                                userId
                        );

        return new UnreadCountResponse(
                count
        );
    }


    // =========================================================
    // MARK ONE AS READ
    // =========================================================

    @Transactional
    public void markAsRead(
            UUID notificationId
    ) {

        UUID userId =
                getCurrentUser().getId();

        int updated =
                notificationRepository.markAsRead(
                        notificationId,
                        userId
                );

        if (updated == 0) {

            throw new NotificationNotFoundException(
                    notificationId
            );
        }
    }


    // =========================================================
    // MARK ALL AS READ
    // =========================================================

    @Transactional
    public void markAllAsRead() {

        UUID userId =
                getCurrentUser().getId();

        notificationRepository.markAllAsRead(
                userId
        );
    }
}