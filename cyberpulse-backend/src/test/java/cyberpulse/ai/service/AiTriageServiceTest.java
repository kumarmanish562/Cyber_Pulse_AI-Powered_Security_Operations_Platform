package cyberpulse.ai.service;

import cyberpulse.ai.dto.AiTriageResponse;
import cyberpulse.common.exception.ResourceNotFoundException;
import cyberpulse.detection.entity.ThreatDetection;
import cyberpulse.event.entity.SecurityEvent;
import cyberpulse.incident.entity.Incident;
import cyberpulse.incident.entity.IncidentSeverity;
import cyberpulse.incident.entity.IncidentStatus;
import cyberpulse.incident.repository.IncidentNoteRepository;
import cyberpulse.incident.repository.IncidentRepository;
import cyberpulse.risk.entity.RiskAssessment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import java.net.InetAddress;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiTriageServiceTest {

    @Mock
    private IncidentRepository incidentRepository;

    @Mock
    private IncidentNoteRepository incidentNoteRepository;

    @Test
    void shouldReturnDeterministicPlaybookWhenApiKeyNotConfigured() throws Exception {
        AiTriageService service = new AiTriageService(
                incidentRepository,
                incidentNoteRepository,
                new ObjectMapper(),
                "gemini",
                "",
                "gemini-2.5-flash"
        );

        UUID id = UUID.randomUUID();
        SecurityEvent event = new SecurityEvent();
        event.setSourceIp(InetAddress.getByName("198.51.100.42"));
        event.setUsername("admin");
        event.setService("auth-api");

        ThreatDetection detection = new ThreatDetection();
        detection.setThreatType("BRUTE_FORCE");
        detection.setEvent(event);

        RiskAssessment risk = new RiskAssessment();
        risk.setRiskScore(88);
        risk.setThreatDetection(detection);

        Incident incident = new Incident();
        incident.setId(id);
        incident.setIncidentNumber("INC-2026-0001");
        incident.setTitle("Brute force detected");
        incident.setSeverity(IncidentSeverity.HIGH);
        incident.setStatus(IncidentStatus.OPEN);
        incident.setRiskAssessment(risk);

        when(incidentRepository.findById(id)).thenReturn(Optional.of(incident));
        when(incidentNoteRepository.findByIncidentIdOrderByCreatedAtAsc(id)).thenReturn(List.of());

        AiTriageResponse response = service.triageIncident(id);

        assertEquals(id, response.incidentId());
        assertEquals("DETERMINISTIC_PLAYBOOK", response.provider());
        assertFalse(response.recommendedActions().isEmpty());
        assertTrue(response.summary().contains("INC-2026-0001"));
    }

    @Test
    void shouldThrowWhenIncidentMissing() {
        AiTriageService service = new AiTriageService(
                incidentRepository,
                incidentNoteRepository,
                new ObjectMapper(),
                "gemini",
                "",
                "gemini-2.5-flash"
        );

        UUID id = UUID.randomUUID();
        when(incidentRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.triageIncident(id));
    }
}
