package cyberpulse.incident.engine;

import cyberpulse.detection.entity.ThreatDetection;
import cyberpulse.incident.dto.CreateIncidentRequest;
import cyberpulse.incident.service.IncidentService;
import cyberpulse.risk.entity.RiskAssessment;
import cyberpulse.risk.entity.RiskSeverity;
import cyberpulse.risk.repository.RiskAssessmentRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncidentEngineTest {

    // =========================================================
    // MOCKS
    // =========================================================

    @Mock
    private RiskAssessmentRepository riskAssessmentRepository;

    @Mock
    private IncidentService incidentService;

    @Mock
    private RiskAssessment assessment;

    @Mock
    private ThreatDetection threatDetection;

    @InjectMocks
    private IncidentEngine engine;

    // =========================================================
    // TEST DATA
    // =========================================================

    private UUID riskAssessmentId;

    // =========================================================
    // SETUP
    // =========================================================

    @BeforeEach
    void setUp() {

        riskAssessmentId = UUID.randomUUID();
    }

    // =========================================================
    // HIGH -> INCIDENT
    // =========================================================

    @Test
    void highRiskShouldCreateIncident() {

        when(
                riskAssessmentRepository.findById(
                        riskAssessmentId
                )
        ).thenReturn(
                Optional.of(assessment)
        );

        when(
                assessment.getId()
        ).thenReturn(
                riskAssessmentId
        );

        when(
                assessment.getSeverity()
        ).thenReturn(
                RiskSeverity.HIGH
        );

        when(
                assessment.getThreatDetection()
        ).thenReturn(
                threatDetection
        );

        when(
                threatDetection.getThreatType()
        ).thenReturn(
                "BRUTE_FORCE_LOGIN"
        );

        when(
                assessment.getRiskScore()
        ).thenReturn(
                85
        );

        engine.evaluateRisk(
                riskAssessmentId
        );

        ArgumentCaptor<CreateIncidentRequest> captor =
                ArgumentCaptor.forClass(
                        CreateIncidentRequest.class
                );

        verify(
                incidentService
        ).createIncident(
                captor.capture()
        );

        CreateIncidentRequest request =
                captor.getValue();

        assertNotNull(request);

        assertEquals(
                riskAssessmentId,
                request.riskAssessmentId()
        );

        assertEquals(
                "Security Incident - BRUTE_FORCE_LOGIN",
                request.title()
        );

        assertNotNull(
                request.description()
        );

        assertTrue(
                request.description()
                        .contains(
                                riskAssessmentId.toString()
                        )
        );

        assertTrue(
                request.description()
                        .contains(
                                "85"
                        )
        );

        assertTrue(
                request.description()
                        .contains(
                                "HIGH"
                        )
        );
    }

    // =========================================================
    // CRITICAL -> INCIDENT
    // =========================================================

    @Test
    void criticalRiskShouldCreateIncident() {

        when(
                riskAssessmentRepository.findById(
                        riskAssessmentId
                )
        ).thenReturn(
                Optional.of(assessment)
        );

        when(
                assessment.getId()
        ).thenReturn(
                riskAssessmentId
        );

        when(
                assessment.getSeverity()
        ).thenReturn(
                RiskSeverity.CRITICAL
        );

        when(
                assessment.getThreatDetection()
        ).thenReturn(
                threatDetection
        );

        when(
                threatDetection.getThreatType()
        ).thenReturn(
                "BRUTE_FORCE_LOGIN"
        );

        when(
                assessment.getRiskScore()
        ).thenReturn(
                95
        );

        engine.evaluateRisk(
                riskAssessmentId
        );

        ArgumentCaptor<CreateIncidentRequest> captor =
                ArgumentCaptor.forClass(
                        CreateIncidentRequest.class
                );

        verify(
                incidentService
        ).createIncident(
                captor.capture()
        );

        CreateIncidentRequest request =
                captor.getValue();

        assertNotNull(request);

        assertEquals(
                riskAssessmentId,
                request.riskAssessmentId()
        );

        assertEquals(
                "Security Incident - BRUTE_FORCE_LOGIN",
                request.title()
        );

        assertTrue(
                request.description()
                        .contains(
                                "95"
                        )
        );

        assertTrue(
                request.description()
                        .contains(
                                "CRITICAL"
                        )
        );
    }

    // =========================================================
    // MEDIUM -> NO INCIDENT
    // =========================================================

    @Test
    void mediumRiskShouldNotCreateIncident() {

        when(
                riskAssessmentRepository.findById(
                        riskAssessmentId
                )
        ).thenReturn(
                Optional.of(assessment)
        );

        when(
                assessment.getSeverity()
        ).thenReturn(
                RiskSeverity.MEDIUM
        );

        engine.evaluateRisk(
                riskAssessmentId
        );

        verify(
                incidentService,
                never()
        ).createIncident(
                any(CreateIncidentRequest.class)
        );
    }

    // =========================================================
    // LOW -> NO INCIDENT
    // =========================================================

    @Test
    void lowRiskShouldNotCreateIncident() {

        when(
                riskAssessmentRepository.findById(
                        riskAssessmentId
                )
        ).thenReturn(
                Optional.of(assessment)
        );

        when(
                assessment.getSeverity()
        ).thenReturn(
                RiskSeverity.LOW
        );

        engine.evaluateRisk(
                riskAssessmentId
        );

        verify(
                incidentService,
                never()
        ).createIncident(
                any(CreateIncidentRequest.class)
        );
    }

    // =========================================================
    // UNKNOWN RISK ASSESSMENT
    // =========================================================

    @Test
    void shouldRejectUnknownRiskAssessment() {

        when(
                riskAssessmentRepository.findById(
                        riskAssessmentId
                )
        ).thenReturn(
                Optional.empty()
        );

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        engine.evaluateRisk(
                                riskAssessmentId
                        )
        );

        verify(
                incidentService,
                never()
        ).createIncident(
                any(CreateIncidentRequest.class)
        );
    }

    // =========================================================
    // VERIFY REPOSITORY LOOKUP
    // =========================================================

    @Test
    void shouldLoadRiskAssessmentBeforeEvaluation() {

        when(
                riskAssessmentRepository.findById(
                        riskAssessmentId
                )
        ).thenReturn(
                Optional.of(assessment)
        );

        when(
                assessment.getSeverity()
        ).thenReturn(
                RiskSeverity.LOW
        );

        engine.evaluateRisk(
                riskAssessmentId
        );

        verify(
                riskAssessmentRepository
        ).findById(
                riskAssessmentId
        );

        verify(
                incidentService,
                never()
        ).createIncident(
                any(CreateIncidentRequest.class)
        );
    }
}