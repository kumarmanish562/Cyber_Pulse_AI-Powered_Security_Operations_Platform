package cyberpulse.notification.controller;

import cyberpulse.notification.dto.NotificationResponse;
import cyberpulse.notification.dto.UnreadCountResponse;
import cyberpulse.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;


    // =========================================================
    // GET MY NOTIFICATIONS
    // =========================================================

    @GetMapping
    public ResponseEntity<Page<NotificationResponse>>
    getMyNotifications(
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                notificationService
                        .getMyNotifications(pageable)
        );
    }


    // =========================================================
    // GET MY UNREAD NOTIFICATIONS
    // =========================================================

    @GetMapping("/unread")
    public ResponseEntity<Page<NotificationResponse>>
    getMyUnreadNotifications(
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                notificationService
                        .getMyUnreadNotifications(
                                pageable
                        )
        );
    }


    // =========================================================
    // UNREAD COUNT
    // =========================================================

    @GetMapping("/unread/count")
    public ResponseEntity<UnreadCountResponse>
    getUnreadCount() {

        return ResponseEntity.ok(
                notificationService
                        .getUnreadCount()
        );
    }


    // =========================================================
    // MARK ONE AS READ
    // =========================================================

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable UUID id
    ) {

        notificationService.markAsRead(id);

        return ResponseEntity.noContent()
                .build();
    }


    // =========================================================
    // MARK ALL AS READ
    // =========================================================

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead() {

        notificationService.markAllAsRead();

        return ResponseEntity.noContent()
                .build();
    }
}