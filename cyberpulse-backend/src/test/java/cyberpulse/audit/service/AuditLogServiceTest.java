package cyberpulse.audit.service;

import cyberpulse.audit.entity.AuditLog;
import cyberpulse.audit.event.AuditAction;
import cyberpulse.audit.mapper.AuditLogMapper;
import cyberpulse.audit.repository.AuditLogRepository;
import cyberpulse.audit.util.AuditDetailsSerializer;
import cyberpulse.audit.util.AuditRequestContext;
import cyberpulse.common.security.CurrentUserService;
import cyberpulse.user.entity.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.ArgumentCaptor;

import java.net.InetAddress;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

class AuditLogServiceTest {

    private AuditLogRepository auditLogRepository;

    private AuditLogMapper auditLogMapper;

    private AuditRequestContext auditRequestContext;

    private CurrentUserService currentUserService;

    private AuditDetailsSerializer auditDetailsSerializer;

    private AuditLogService auditLogService;

    private UUID userId;

    private User user;


    @BeforeEach
    void setUp() {

        auditLogRepository =
                mock(AuditLogRepository.class);

        auditLogMapper =
                mock(AuditLogMapper.class);

        auditRequestContext =
                mock(AuditRequestContext.class);

        currentUserService =
                mock(CurrentUserService.class);

        auditDetailsSerializer =
                mock(AuditDetailsSerializer.class);


        auditLogService =
                new AuditLogService(

                        auditLogRepository,

                        auditLogMapper,

                        auditRequestContext,

                        currentUserService,

                        auditDetailsSerializer
                );


        userId =
                UUID.randomUUID();


        user =
                mock(User.class);


        when(user.getId())
                .thenReturn(userId);


        when(currentUserService.getCurrentUser())
                .thenReturn(
                        Optional.of(user)
                );


        try {

            when(
                    auditRequestContext
                            .getIpAddress()
            )
                    .thenReturn(
                            InetAddress.getByName(
                                    "127.0.0.1"
                            )
                    );

        } catch (Exception exception) {

            throw new RuntimeException(
                    exception
            );
        }


        when(
                auditRequestContext
                        .getUserAgent()
        )
                .thenReturn(
                        "JUnit"
                );
    }


    @Test
    void shouldRecordAuditLog() {

        UUID incidentId =
                UUID.randomUUID();


        Map<String, Object> details =
                Map.of(

                        "oldStatus",
                        "OPEN",

                        "newStatus",
                        "INVESTIGATING"
                );


        when(
                auditDetailsSerializer
                        .serialize(details)
        )
                .thenReturn(
                        "{\"oldStatus\":\"OPEN\",\"newStatus\":\"INVESTIGATING\"}"
                );


        auditLogService.record(

                AuditAction.INCIDENT_STATUS_CHANGED,

                "Incident",

                incidentId,

                details
        );


        ArgumentCaptor<AuditLog> captor =
                ArgumentCaptor.forClass(
                        AuditLog.class
                );


        verify(
                auditLogRepository
        )
                .save(
                        captor.capture()
                );


        AuditLog saved =
                captor.getValue();


        assertNotNull(saved);


        assertEquals(
                userId,
                saved.getUser().getId()
        );


        assertEquals(
                "INCIDENT_STATUS_CHANGED",
                saved.getAction()
        );


        assertEquals(
                "Incident",
                saved.getEntityType()
        );


        assertEquals(
                incidentId,
                saved.getEntityId()
        );


        assertEquals(
                "127.0.0.1",
                saved.getIpAddress()
                        .getHostAddress()
        );


        assertEquals(
                "JUnit",
                saved.getUserAgent()
        );


        assertEquals(
                "{\"oldStatus\":\"OPEN\",\"newStatus\":\"INVESTIGATING\"}",
                saved.getDetails()
        );


        assertNotNull(
                saved.getCreatedAt()
        );


        verify(
                auditDetailsSerializer
        )
                .serialize(details);


        verify(
                auditRequestContext
        )
                .getIpAddress();


        verify(
                auditRequestContext
        )
                .getUserAgent();


        verify(
                currentUserService
        )
                .getCurrentUser();


        verify(
                auditLogRepository
        )
                .save(
                        any(AuditLog.class)
                );
    }


    @Test
    void shouldAllowAuditWithoutAuthenticatedUser() {

        when(
                currentUserService
                        .getCurrentUser()
        )
                .thenReturn(
                        Optional.empty()
                );


        Map<String, Object> details =
                Map.of(
                        "username",
                        "unknown-user"
                );


        when(
                auditDetailsSerializer
                        .serialize(details)
        )
                .thenReturn(
                        "{\"username\":\"unknown-user\"}"
                );


        auditLogService.record(

                AuditAction.LOGIN_FAILED,

                "User",

                null,

                details
        );


        ArgumentCaptor<AuditLog> captor =
                ArgumentCaptor.forClass(
                        AuditLog.class
                );


        verify(
                auditLogRepository
        )
                .save(
                        captor.capture()
                );


        AuditLog saved =
                captor.getValue();


        assertNotNull(saved);


        assertNull(
                saved.getUser()
        );


        assertEquals(
                "LOGIN_FAILED",
                saved.getAction()
        );


        assertEquals(
                "User",
                saved.getEntityType()
        );


        assertNull(
                saved.getEntityId()
        );


        assertEquals(
                "{\"username\":\"unknown-user\"}",
                saved.getDetails()
        );


        assertNotNull(
                saved.getCreatedAt()
        );
    }


    @Test
    void shouldRecordAuditWithNullDetails() {

        auditLogService.record(

                AuditAction.LOGIN_SUCCESS,

                "User",

                userId,

                null
        );


        ArgumentCaptor<AuditLog> captor =
                ArgumentCaptor.forClass(
                        AuditLog.class
                );


        verify(
                auditLogRepository
        )
                .save(
                        captor.capture()
                );


        AuditLog saved =
                captor.getValue();


        assertNotNull(saved);


        assertEquals(
                userId,
                saved.getUser().getId()
        );


        assertEquals(
                "LOGIN_SUCCESS",
                saved.getAction()
        );


        assertNull(
                saved.getDetails()
        );


        assertNotNull(
                saved.getCreatedAt()
        );


        verify(
                auditDetailsSerializer,
                never()
        )
                .serialize(any());
    }
}