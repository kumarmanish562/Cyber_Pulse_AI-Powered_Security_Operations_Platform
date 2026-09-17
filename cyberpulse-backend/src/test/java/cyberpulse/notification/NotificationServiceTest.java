package cyberpulse.notification;

import cyberpulse.notification.dto.NotificationResponse;
import cyberpulse.notification.dto.UnreadCountResponse;
import cyberpulse.notification.entity.Notification;
import cyberpulse.notification.mapper.NotificationMapper;
import cyberpulse.notification.repository.NotificationRepository;
import cyberpulse.notification.service.NotificationService;
import cyberpulse.user.entity.User;
import cyberpulse.user.repository.UserRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationMapper notificationMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationService notificationService;

    private UUID userId;
    private User user;


    // =========================================================
    // SETUP
    // =========================================================

    @BeforeEach
    void setUp() {

        userId = UUID.randomUUID();

        user = new User();

        user.setId(userId);
        user.setUsername("analyst1");
        user.setEnabled(true);
        user.setLocked(false);
    }


    // =========================================================
    // AUTHENTICATION HELPER
    // =========================================================

    private void authenticateUser() {

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "analyst1",
                        null,
                        List.of()
                );

        SecurityContext context =
                SecurityContextHolder.createEmptyContext();

        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);

        when(
                userRepository.findByUsernameIgnoreCase("analyst1")
        ).thenReturn(
                Optional.of(user)
        );
    }


    // =========================================================
    // CLEANUP
    // =========================================================

    @AfterEach
    void tearDown() {

        SecurityContextHolder.clearContext();
    }


    // =========================================================
    // CREATE NOTIFICATION
    // =========================================================

    @Test
    void shouldCreateNotification() {

        UUID entityId = UUID.randomUUID();

        when(
                userRepository.findById(userId)
        ).thenReturn(
                Optional.of(user)
        );

        Notification saved = new Notification();

        when(
                notificationRepository.save(
                        org.mockito.ArgumentMatchers.any(Notification.class)
                )
        ).thenReturn(saved);

        Notification result =
                notificationService.create(
                        userId,
                        "INCIDENT_CREATED",
                        "Incident created",
                        "A new incident was created.",
                        "Incident",
                        entityId
                );

        assertThat(result)
                .isSameAs(saved);

        verify(
                userRepository
        ).findById(userId);

        verify(
                notificationRepository
        ).save(
                org.mockito.ArgumentMatchers.any(Notification.class)
        );
    }


    // =========================================================
    // GET MY NOTIFICATIONS
    // =========================================================

    @Test
    void shouldReturnMyNotifications() {

        authenticateUser();

        Pageable pageable =
                PageRequest.of(0, 10);

        UUID notificationId =
                UUID.randomUUID();

        UUID entityId =
                UUID.randomUUID();

        Notification notification =
                new Notification();

        notification.setId(notificationId);
        notification.setUser(user);
        notification.setTitle("Incident created");
        notification.setMessage("A new incident was created.");
        notification.setType("INCIDENT_CREATED");
        notification.setEntityType("Incident");
        notification.setEntityId(entityId);
        notification.setRead(false);
        notification.setCreatedAt(Instant.now());

        NotificationResponse response =
                new NotificationResponse(
                        notificationId,
                        "Incident created",
                        "A new incident was created.",
                        "INCIDENT_CREATED",
                        "Incident",
                        entityId,
                        false,
                        notification.getCreatedAt()
                );

        Page<Notification> page =
                new PageImpl<>(
                        List.of(notification),
                        pageable,
                        1
                );

        when(
                notificationRepository
                        .findByUser_IdOrderByCreatedAtDesc(
                                userId,
                                pageable
                        )
        ).thenReturn(page);

        when(
                notificationMapper.toResponse(notification)
        ).thenReturn(response);

        Page<NotificationResponse> result =
                notificationService.getMyNotifications(
                        pageable
                );

        assertThat(result)
                .isNotNull();

        assertThat(result.getContent())
                .hasSize(1);

        assertThat(
                result.getContent()
                        .get(0)
                        .id()
        ).isEqualTo(notificationId);

        assertThat(
                result.getContent()
                        .get(0)
                        .title()
        ).isEqualTo("Incident created");

        assertThat(
                result.getContent()
                        .get(0)
                        .type()
        ).isEqualTo("INCIDENT_CREATED");

        assertThat(
                result.getContent()
                        .get(0)
                        .entityType()
        ).isEqualTo("Incident");

        assertThat(
                result.getContent()
                        .get(0)
                        .entityId()
        ).isEqualTo(entityId);

        assertThat(
                result.getContent()
                        .get(0)
                        .read()
        ).isFalse();

        verify(
                userRepository
        ).findByUsernameIgnoreCase("analyst1");

        verify(
                notificationRepository
        ).findByUser_IdOrderByCreatedAtDesc(
                userId,
                pageable
        );

        verify(
                notificationMapper
        ).toResponse(notification);
    }


    // =========================================================
    // GET UNREAD COUNT
    // =========================================================

    @Test
    void shouldReturnUnreadCount() {

        authenticateUser();

        when(
                notificationRepository
                        .countByUser_IdAndReadFalse(userId)
        ).thenReturn(5L);

        UnreadCountResponse result =
                notificationService.getUnreadCount();

        assertThat(result)
                .isNotNull();

        assertThat(result.count())
                .isEqualTo(5L);

        verify(
                userRepository
        ).findByUsernameIgnoreCase("analyst1");

        verify(
                notificationRepository
        ).countByUser_IdAndReadFalse(userId);
    }


    // =========================================================
    // MARK ONE AS READ
    // =========================================================

    @Test
    void shouldMarkNotificationAsRead() {

        authenticateUser();

        UUID notificationId =
                UUID.randomUUID();

        when(
                notificationRepository.markAsRead(
                        notificationId,
                        userId
                )
        ).thenReturn(1);

        notificationService.markAsRead(
                notificationId
        );

        verify(
                userRepository
        ).findByUsernameIgnoreCase("analyst1");

        verify(
                notificationRepository
        ).markAsRead(
                notificationId,
                userId
        );
    }


    // =========================================================
    // MARK ALL AS READ
    // =========================================================

    @Test
    void shouldMarkAllNotificationsAsRead() {

        authenticateUser();

        when(
                notificationRepository.markAllAsRead(userId)
        ).thenReturn(5);

        notificationService.markAllAsRead();

        verify(
                userRepository
        ).findByUsernameIgnoreCase("analyst1");

        verify(
                notificationRepository
        ).markAllAsRead(userId);
    }
}