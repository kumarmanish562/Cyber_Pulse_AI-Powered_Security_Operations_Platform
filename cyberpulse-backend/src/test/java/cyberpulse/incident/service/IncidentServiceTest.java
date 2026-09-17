package cyberpulse.incident.service;

import cyberpulse.event.entity.SecurityEvent;
import cyberpulse.event.repository.SecurityEventRepository;

import cyberpulse.incident.dto.CreateIncidentRequest;
import cyberpulse.incident.dto.IncidentEventResponse;
import cyberpulse.incident.dto.IncidentNoteResponse;
import cyberpulse.incident.dto.IncidentResponse;

import cyberpulse.incident.entity.Incident;
import cyberpulse.incident.entity.IncidentEvent;
import cyberpulse.incident.entity.IncidentNote;
import cyberpulse.incident.entity.IncidentSeverity;
import cyberpulse.incident.entity.IncidentStatus;

import cyberpulse.incident.mapper.IncidentEventMapper;
import cyberpulse.incident.mapper.IncidentMapper;
import cyberpulse.incident.mapper.IncidentNoteMapper;

import cyberpulse.incident.repository.IncidentEventRepository;
import cyberpulse.incident.repository.IncidentNoteRepository;
import cyberpulse.incident.repository.IncidentRepository;

import cyberpulse.risk.entity.RiskAssessment;
import cyberpulse.risk.entity.RiskSeverity;
import cyberpulse.risk.repository.RiskAssessmentRepository;

import cyberpulse.user.entity.User;
import cyberpulse.user.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.context.ApplicationEventPublisher;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


/**
 * Unit tests for IncidentService.
 *
 * These tests verify business rules only.
 *
 * No Spring ApplicationContext is loaded.
 * No PostgreSQL database is required.
 */
@ExtendWith(MockitoExtension.class)
class IncidentServiceTest {

    // =========================================================
    // MOCKS
    // =========================================================

    @Mock
    private IncidentRepository incidentRepository;

    @Mock
    private RiskAssessmentRepository riskAssessmentRepository;

    @Mock
    private IncidentEventRepository incidentEventRepository;

    @Mock
    private IncidentNoteRepository incidentNoteRepository;

    @Mock
    private SecurityEventRepository securityEventRepository;

    @Mock
    private IncidentNumberGenerator incidentNumberGenerator;

    @Mock
    private IncidentMapper incidentMapper;

    @Mock
    private IncidentEventMapper incidentEventMapper;

    @Mock
    private IncidentNoteMapper incidentNoteMapper;

    @Mock
    private UserRepository userRepository;

    /*
     * PHASE 10
     *
     * Required because IncidentService now
     * publishes application events.
     */
    @Mock
    private ApplicationEventPublisher eventPublisher;

    /*
     * Mockito automatically injects all mocks
     * into IncidentService.
     */
    @InjectMocks
    private IncidentService service;


    // =========================================================
    // TEST DATA
    // =========================================================

    private UUID incidentId;
    private UUID riskAssessmentId;
    private UUID userId;
    private UUID eventId;

    private final String username = "analyst1";

    private Incident incident;
    private RiskAssessment assessment;
    private SecurityEvent event;
    private User user;

    private IncidentResponse incidentResponse;
    private IncidentNoteResponse noteResponse;
    private IncidentEventResponse eventResponse;


    // =========================================================
    // SETUP
    // =========================================================

    @BeforeEach
    void setUp() {

        incidentId =
                UUID.randomUUID();

        riskAssessmentId =
                UUID.randomUUID();

        userId =
                UUID.randomUUID();

        eventId =
                UUID.randomUUID();


        // -----------------------------------------------------
        // Incident
        // -----------------------------------------------------

        incident =
                new Incident();

        incident.setId(
                incidentId
        );

        incident.setIncidentNumber(
                "INC-2026-000001"
        );

        incident.setTitle(
                "Test Incident"
        );

        incident.setDescription(
                "Test incident description"
        );

        incident.setStatus(
                IncidentStatus.OPEN
        );

        incident.setSeverity(
                IncidentSeverity.HIGH
        );

        incident.setAssignedTo(
                null
        );


        // -----------------------------------------------------
        // Risk Assessment
        // -----------------------------------------------------

        assessment =
                new RiskAssessment();

        assessment.setId(
                riskAssessmentId
        );

        assessment.setSeverity(
                RiskSeverity.HIGH
        );


        /*
         * IMPORTANT:
         *
         * IncidentService Phase 10 accesses:
         *
         * savedIncident
         *     .getRiskAssessment()
         *     .getId()
         *
         * Therefore the test Incident must have
         * the RiskAssessment attached.
         */
        incident.setRiskAssessment(
                assessment
        );


        // -----------------------------------------------------
        // Security Event
        // -----------------------------------------------------

        event =
                new SecurityEvent();


        // -----------------------------------------------------
        // User
        // -----------------------------------------------------

        user =
                new User();

        user.setId(
                userId
        );

        user.setUsername(
                username
        );

        user.setEmail(
                "analyst1@cyberpulse.local"
        );


        // -----------------------------------------------------
        // Mapper responses
        // -----------------------------------------------------

        incidentResponse =
                mock(IncidentResponse.class);

        noteResponse =
                mock(IncidentNoteResponse.class);

        eventResponse =
                mock(IncidentEventResponse.class);
    }


    // =========================================================
    // CREATE INCIDENT
    // =========================================================

    @Test
    void shouldCreateIncident() {

        CreateIncidentRequest request =
                new CreateIncidentRequest(
                        riskAssessmentId,
                        "Test Incident",
                        "Test incident description"
                );


        when(
                incidentRepository.existsByRiskAssessmentId(
                        riskAssessmentId
                )
        ).thenReturn(false);


        when(
                riskAssessmentRepository.findById(
                        riskAssessmentId
                )
        ).thenReturn(
                Optional.of(assessment)
        );


        when(
                incidentNumberGenerator.next()
        ).thenReturn(
                "INC-2026-000001"
        );


        when(
                incidentRepository.save(
                        any(Incident.class)
                )
        ).thenReturn(
                incident
        );


        when(
                incidentMapper.toResponse(
                        incident
                )
        ).thenReturn(
                incidentResponse
        );


        IncidentResponse result =
                service.createIncident(
                        request
                );


        assertNotNull(
                result
        );


        verify(
                incidentRepository
        ).existsByRiskAssessmentId(
                riskAssessmentId
        );


        verify(
                riskAssessmentRepository
        ).findById(
                riskAssessmentId
        );


        verify(
                incidentNumberGenerator
        ).next();


        verify(
                incidentRepository
        ).save(
                any(Incident.class)
        );


        verify(
                incidentMapper
        ).toResponse(
                incident
        );


        /*
         * Phase 10 verification.
         */
        verify(
                eventPublisher
        ).publishEvent(
                any(Object.class)
        );
    }


    // =========================================================
    // DUPLICATE RISK ASSESSMENT
    // =========================================================

    @Test
    void shouldRejectDuplicateRiskAssessment() {

        CreateIncidentRequest request =
                new CreateIncidentRequest(
                        riskAssessmentId,
                        "Duplicate Incident",
                        "Duplicate description"
                );


        when(
                incidentRepository.existsByRiskAssessmentId(
                        riskAssessmentId
                )
        ).thenReturn(true);


        assertThrows(
                IllegalStateException.class,
                () ->
                        service.createIncident(
                                request
                        )
        );


        verify(
                incidentRepository
        ).existsByRiskAssessmentId(
                riskAssessmentId
        );


        verifyNoInteractions(
                riskAssessmentRepository,
                incidentNumberGenerator
        );


        verifyNoInteractions(
                eventPublisher
        );
    }


    // =========================================================
    // RISK ASSESSMENT NOT FOUND
    // =========================================================

    @Test
    void shouldRejectWhenRiskAssessmentDoesNotExist() {

        CreateIncidentRequest request =
                new CreateIncidentRequest(
                        riskAssessmentId,
                        "Test Incident",
                        "Description"
                );


        when(
                incidentRepository.existsByRiskAssessmentId(
                        riskAssessmentId
                )
        ).thenReturn(false);


        when(
                riskAssessmentRepository.findById(
                        riskAssessmentId
                )
        ).thenReturn(
                Optional.empty()
        );


        assertThrows(
                EntityNotFoundException.class,
                () ->
                        service.createIncident(
                                request
                        )
        );


        verify(
                riskAssessmentRepository
        ).findById(
                riskAssessmentId
        );


        verify(
                incidentRepository,
                never()
        ).save(
                any()
        );


        verify(
                incidentNumberGenerator,
                never()
        ).next();


        verifyNoInteractions(
                eventPublisher
        );
    }


    // =========================================================
    // GET INCIDENT
    // =========================================================

    @Test
    void shouldGetIncidentById() {

        when(
                incidentRepository.findById(
                        incidentId
                )
        ).thenReturn(
                Optional.of(incident)
        );


        when(
                incidentMapper.toResponse(
                        incident
                )
        ).thenReturn(
                incidentResponse
        );


        IncidentResponse result =
                service.getById(
                        incidentId
                );


        assertNotNull(
                result
        );


        verify(
                incidentRepository
        ).findById(
                incidentId
        );


        verify(
                incidentMapper
        ).toResponse(
                incident
        );
    }


    // =========================================================
    // UNKNOWN INCIDENT
    // =========================================================

    @Test
    void shouldRejectUnknownIncidentId() {

        when(
                incidentRepository.findById(
                        incidentId
                )
        ).thenReturn(
                Optional.empty()
        );


        assertThrows(
                EntityNotFoundException.class,
                () ->
                        service.getById(
                                incidentId
                        )
        );


        verify(
                incidentRepository
        ).findById(
                incidentId
        );
    }


    // =========================================================
    // GET ALL INCIDENTS
    // =========================================================

    @Test
    void shouldGetAllIncidents() {

        PageRequest pageable =
                PageRequest.of(
                        0,
                        20
                );


        Page<Incident> page =
                new PageImpl<>(
                        List.of(incident),
                        pageable,
                        1
                );


        when(
                incidentRepository.findAll(
                        pageable
                )
        ).thenReturn(
                page
        );


        when(
                incidentMapper.toResponse(
                        incident
                )
        ).thenReturn(
                incidentResponse
        );


        Page<IncidentResponse> result =
                service.getAll(
                        pageable
                );


        assertNotNull(
                result
        );


        assertEquals(
                1,
                result.getTotalElements()
        );


        verify(
                incidentRepository
        ).findAll(
                pageable
        );


        verify(
                incidentMapper
        ).toResponse(
                incident
        );
    }


    // =========================================================
    // OPEN -> INVESTIGATING
    // =========================================================

    @Test
    void shouldAllowOpenToInvestigating() {

        incident.setStatus(
                IncidentStatus.OPEN
        );


        when(
                incidentRepository.findById(
                        incidentId
                )
        ).thenReturn(
                Optional.of(incident)
        );


        when(
                incidentRepository.save(
                        incident
                )
        ).thenReturn(
                incident
        );


        when(
                incidentMapper.toResponse(
                        incident
                )
        ).thenReturn(
                incidentResponse
        );


        IncidentResponse result =
                service.updateStatus(
                        incidentId,
                        IncidentStatus.INVESTIGATING
                );


        assertNotNull(
                result
        );


        assertEquals(
                IncidentStatus.INVESTIGATING,
                incident.getStatus()
        );


        verify(
                incidentRepository
        ).save(
                incident
        );


        verify(
                incidentMapper
        ).toResponse(
                incident
        );


        /*
         * Phase 10.
         */
        verify(
                eventPublisher
        ).publishEvent(
                any(Object.class)
        );
    }


    // =========================================================
    // INVESTIGATING -> RESOLVED
    // =========================================================

    @Test
    void shouldAllowInvestigatingToResolved() {

        incident.setStatus(
                IncidentStatus.INVESTIGATING
        );


        when(
                incidentRepository.findById(
                        incidentId
                )
        ).thenReturn(
                Optional.of(incident)
        );


        when(
                incidentRepository.save(
                        incident
                )
        ).thenReturn(
                incident
        );


        when(
                incidentMapper.toResponse(
                        incident
                )
        ).thenReturn(
                incidentResponse
        );


        IncidentResponse result =
                service.updateStatus(
                        incidentId,
                        IncidentStatus.RESOLVED
                );


        assertNotNull(
                result
        );


        assertEquals(
                IncidentStatus.RESOLVED,
                incident.getStatus()
        );


        assertNotNull(
                incident.getResolvedAt()
        );


        verify(
                incidentRepository
        ).save(
                incident
        );


        verify(
                eventPublisher
        ).publishEvent(
                any(Object.class)
        );
    }


    // =========================================================
    // RESOLVED -> CLOSED
    // =========================================================

    @Test
    void shouldAllowResolvedToClosed() {

        incident.setStatus(
                IncidentStatus.RESOLVED
        );


        incident.setResolvedAt(
                Instant.now()
        );


        when(
                incidentRepository.findById(
                        incidentId
                )
        ).thenReturn(
                Optional.of(incident)
        );


        when(
                incidentRepository.save(
                        incident
                )
        ).thenReturn(
                incident
        );


        when(
                incidentMapper.toResponse(
                        incident
                )
        ).thenReturn(
                incidentResponse
        );


        IncidentResponse result =
                service.updateStatus(
                        incidentId,
                        IncidentStatus.CLOSED
                );


        assertNotNull(
                result
        );


        assertEquals(
                IncidentStatus.CLOSED,
                incident.getStatus()
        );


        assertNotNull(
                incident.getClosedAt()
        );


        verify(
                incidentRepository
        ).save(
                incident
        );


        verify(
                eventPublisher
        ).publishEvent(
                any(Object.class)
        );
    }


    // =========================================================
    // OPEN -> CLOSED REJECTED
    // =========================================================

    @Test
    void shouldRejectOpenToClosed() {

        incident.setStatus(
                IncidentStatus.OPEN
        );


        when(
                incidentRepository.findById(
                        incidentId
                )
        ).thenReturn(
                Optional.of(incident)
        );


        assertThrows(
                IllegalStateException.class,
                () ->
                        service.updateStatus(
                                incidentId,
                                IncidentStatus.CLOSED
                        )
        );


        verify(
                incidentRepository,
                never()
        ).save(
                any()
        );


        verifyNoInteractions(
                eventPublisher
        );
    }


    // =========================================================
    // CLOSED -> OPEN REJECTED
    // =========================================================

    @Test
    void shouldRejectClosedToOpen() {

        incident.setStatus(
                IncidentStatus.CLOSED
        );


        when(
                incidentRepository.findById(
                        incidentId
                )
        ).thenReturn(
                Optional.of(incident)
        );


        assertThrows(
                IllegalStateException.class,
                () ->
                        service.updateStatus(
                                incidentId,
                                IncidentStatus.OPEN
                        )
        );


        verify(
                incidentRepository,
                never()
        ).save(
                any()
        );


        verifyNoInteractions(
                eventPublisher
        );
    }


    // =========================================================
    // SAME STATUS REJECTED
    // =========================================================

    @Test
    void shouldRejectSameStatus() {

        incident.setStatus(
                IncidentStatus.OPEN
        );


        when(
                incidentRepository.findById(
                        incidentId
                )
        ).thenReturn(
                Optional.of(incident)
        );


        assertThrows(
                IllegalStateException.class,
                () ->
                        service.updateStatus(
                                incidentId,
                                IncidentStatus.OPEN
                        )
        );


        verify(
                incidentRepository,
                never()
        ).save(
                any()
        );


        verifyNoInteractions(
                eventPublisher
        );
    }


    // =========================================================
    // ASSIGN INCIDENT
    // =========================================================

    @Test
    void shouldAssignIncident() {

        when(
                incidentRepository.findById(
                        incidentId
                )
        ).thenReturn(
                Optional.of(incident)
        );


        when(
                userRepository.findById(
                        userId
                )
        ).thenReturn(
                Optional.of(user)
        );


        when(
                incidentRepository.save(
                        incident
                )
        ).thenReturn(
                incident
        );


        when(
                incidentMapper.toResponse(
                        incident
                )
        ).thenReturn(
                incidentResponse
        );


        IncidentResponse response =
                service.assignIncident(
                        incidentId,
                        userId
                );


        assertNotNull(
                response
        );


        verify(
                incidentRepository
        ).findById(
                incidentId
        );


        verify(
                userRepository
        ).findById(
                userId
        );


        assertEquals(
                userId,
                incident.getAssignedTo()
        );


        verify(
                incidentRepository
        ).save(
                incident
        );


        verify(
                incidentMapper
        ).toResponse(
                incident
        );


        /*
         * Phase 10.
         */
        verify(
                eventPublisher
        ).publishEvent(
                any(Object.class)
        );
    }


    // =========================================================
    // CLOSED INCIDENT CANNOT BE ASSIGNED
    // =========================================================

    @Test
    void shouldRejectAssignmentToClosedIncident() {

        incident.setStatus(
                IncidentStatus.CLOSED
        );


        when(
                incidentRepository.findById(
                        incidentId
                )
        ).thenReturn(
                Optional.of(incident)
        );


        assertThrows(
                IllegalStateException.class,
                () ->
                        service.assignIncident(
                                incidentId,
                                userId
                        )
        );


        verify(
                incidentRepository,
                never()
        ).save(
                any()
        );


        verifyNoInteractions(
                eventPublisher
        );
    }


    // =========================================================
    // ADD NOTE
    // =========================================================

    @Test
    void shouldAddNote() {

        String noteText =
                "Investigation confirmed suspicious activity.";


        IncidentNote note =
                new IncidentNote();


        when(
                incidentRepository.findById(
                        incidentId
                )
        ).thenReturn(
                Optional.of(incident)
        );


        when(
                userRepository.findByUsernameIgnoreCase(
                        username
                )
        ).thenReturn(
                Optional.of(user)
        );


        when(
                incidentNoteRepository.save(
                        any(IncidentNote.class)
                )
        ).thenReturn(
                note
        );


        when(
                incidentNoteMapper.toResponse(
                        note
                )
        ).thenReturn(
                noteResponse
        );


        IncidentNoteResponse result =
                service.addNote(
                        incidentId,
                        username,
                        noteText
                );


        assertNotNull(
                result
        );


        verify(
                incidentRepository
        ).findById(
                incidentId
        );


        verify(
                userRepository
        ).findByUsernameIgnoreCase(
                username
        );


        verify(
                incidentNoteRepository
        ).save(
                any(IncidentNote.class)
        );


        verify(
                incidentNoteMapper
        ).toResponse(
                note
        );


        /*
         * Phase 10.
         */
        verify(
                eventPublisher
        ).publishEvent(
                any(Object.class)
        );
    }


    // =========================================================
    // CLOSED INCIDENT CANNOT RECEIVE NOTE
    // =========================================================

    @Test
    void shouldRejectNoteForClosedIncident() {

        incident.setStatus(
                IncidentStatus.CLOSED
        );


        when(
                incidentRepository.findById(
                        incidentId
                )
        ).thenReturn(
                Optional.of(incident)
        );


        assertThrows(
                IllegalStateException.class,
                () ->
                        service.addNote(
                                incidentId,
                                username,
                                "Test note"
                        )
        );


        verify(
                userRepository,
                never()
        ).findByUsernameIgnoreCase(
                anyString()
        );


        verify(
                incidentNoteRepository,
                never()
        ).save(
                any()
        );


        verifyNoInteractions(
                eventPublisher
        );
    }


    // =========================================================
    // GET NOTES
    // =========================================================

    @Test
    void shouldGetNotes() {

        IncidentNote note =
                new IncidentNote();


        when(
                incidentRepository.findById(
                        incidentId
                )
        ).thenReturn(
                Optional.of(incident)
        );


        when(
                incidentNoteRepository
                        .findByIncidentIdOrderByCreatedAtAsc(
                                incidentId
                        )
        ).thenReturn(
                List.of(note)
        );


        when(
                incidentNoteMapper.toResponse(
                        note
                )
        ).thenReturn(
                noteResponse
        );


        List<IncidentNoteResponse> result =
                service.getNotes(
                        incidentId
                );


        assertNotNull(
                result
        );


        assertEquals(
                1,
                result.size()
        );


        verify(
                incidentNoteRepository
        ).findByIncidentIdOrderByCreatedAtAsc(
                incidentId
        );


        verify(
                incidentNoteMapper
        ).toResponse(
                note
        );
    }


    // =========================================================
    // ADD EVENT
    // =========================================================

    @Test
    void shouldAddEvent() {

        IncidentEvent incidentEvent =
                new IncidentEvent();


        when(
                incidentRepository.findById(
                        incidentId
                )
        ).thenReturn(
                Optional.of(incident)
        );


        when(
                securityEventRepository.findById(
                        eventId
                )
        ).thenReturn(
                Optional.of(event)
        );


        when(
                incidentEventRepository
                        .existsByIncident_IdAndEvent_Id(
                                incidentId,
                                eventId
                        )
        ).thenReturn(false);


        when(
                incidentEventRepository.save(
                        any(IncidentEvent.class)
                )
        ).thenReturn(
                incidentEvent
        );


        when(
                incidentEventMapper.toResponse(
                        incidentEvent
                )
        ).thenReturn(
                eventResponse
        );


        IncidentEventResponse result =
                service.addEvent(
                        incidentId,
                        eventId
                );


        assertNotNull(
                result
        );


        verify(
                securityEventRepository
        ).findById(
                eventId
        );


        verify(
                incidentEventRepository
        ).existsByIncident_IdAndEvent_Id(
                incidentId,
                eventId
        );


        verify(
                incidentEventRepository
        ).save(
                any(IncidentEvent.class)
        );
    }


    // =========================================================
    // DUPLICATE EVENT
    // =========================================================

    @Test
    void shouldRejectDuplicateEvent() {

        when(
                incidentRepository.findById(
                        incidentId
                )
        ).thenReturn(
                Optional.of(incident)
        );


        when(
                securityEventRepository.findById(
                        eventId
                )
        ).thenReturn(
                Optional.of(event)
        );


        when(
                incidentEventRepository
                        .existsByIncident_IdAndEvent_Id(
                                incidentId,
                                eventId
                        )
        ).thenReturn(true);


        assertThrows(
                IllegalStateException.class,
                () ->
                        service.addEvent(
                                incidentId,
                                eventId
                        )
        );


        verify(
                incidentEventRepository,
                never()
        ).save(
                any()
        );
    }


    // =========================================================
    // UNKNOWN SECURITY EVENT
    // =========================================================

    @Test
    void shouldRejectUnknownSecurityEvent() {

        when(
                incidentRepository.findById(
                        incidentId
                )
        ).thenReturn(
                Optional.of(incident)
        );


        when(
                securityEventRepository.findById(
                        eventId
                )
        ).thenReturn(
                Optional.empty()
        );


        assertThrows(
                EntityNotFoundException.class,
                () ->
                        service.addEvent(
                                incidentId,
                                eventId
                        )
        );


        verify(
                incidentEventRepository,
                never()
        ).save(
                any()
        );
    }


    // =========================================================
    // GET EVENTS
    // =========================================================

    @Test
    void shouldGetEvents() {

        IncidentEvent incidentEvent =
                new IncidentEvent();


        when(
                incidentRepository.findById(
                        incidentId
                )
        ).thenReturn(
                Optional.of(incident)
        );


        when(
                incidentEventRepository.findByIncident_Id(
                        incidentId
                )
        ).thenReturn(
                List.of(incidentEvent)
        );


        when(
                incidentEventMapper.toResponse(
                        incidentEvent
                )
        ).thenReturn(
                eventResponse
        );


        List<IncidentEventResponse> result =
                service.getEvents(
                        incidentId
                );


        assertNotNull(
                result
        );


        assertEquals(
                1,
                result.size()
        );


        verify(
                incidentEventRepository
        ).findByIncident_Id(
                incidentId
        );


        verify(
                incidentEventMapper
        ).toResponse(
                incidentEvent
        );
    }
}