package cyberpulse.playbook.service;

import cyberpulse.alerting.AlertWebhookService;
import cyberpulse.blocklist.service.BlocklistService;
import cyberpulse.detection.entity.ThreatDetection;
import cyberpulse.event.entity.SecurityEvent;
import cyberpulse.incident.entity.Incident;
import cyberpulse.incident.entity.IncidentSeverity;
import cyberpulse.incident.entity.IncidentStatus;
import cyberpulse.incident.repository.IncidentRepository;
import cyberpulse.incident.service.IncidentService;
import cyberpulse.playbook.dto.ExecutePlaybookRequest.PlaybookAction;
import cyberpulse.playbook.dto.ExecutePlaybookResponse;
import cyberpulse.risk.entity.RiskAssessment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.InetAddress;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaybookServiceTest {

    @Mock
    private IncidentRepository incidentRepository;
    @Mock
    private IncidentService incidentService;
    @Mock
    private BlocklistService blocklistService;
    @Mock
    private AlertWebhookService alertWebhookService;

    @InjectMocks
    private PlaybookService playbookService;

    @Test
    void shouldContainIncidentAndBlockSourceIp() throws Exception {
        UUID id = UUID.randomUUID();
        SecurityEvent ev = new SecurityEvent();
        ev.setSourceIp(InetAddress.getByName("198.51.100.88"));

        ThreatDetection det = new ThreatDetection();
        det.setEvent(ev);

        RiskAssessment risk = new RiskAssessment();
        risk.setThreatDetection(det);

        Incident inc = new Incident();
        inc.setId(id);
        inc.setIncidentNumber("INC-2026-0500");
        inc.setStatus(IncidentStatus.OPEN);
        inc.setSeverity(IncidentSeverity.HIGH);
        inc.setRiskAssessment(risk);

        when(incidentRepository.findById(id)).thenReturn(Optional.of(inc));

        ExecutePlaybookResponse res = playbookService.execute(
                id,
                PlaybookAction.CONTAIN_AND_BLOCK_IP,
                "analyst"
        );

        assertEquals("CONTAIN_AND_BLOCK_IP", res.action());
        verify(incidentService).updateStatus(id, IncidentStatus.INVESTIGATING);
        verify(blocklistService).blockIpAutomatically(eq("198.51.100.88"), anyString(), eq("HIGH"), any());
        verify(incidentService).addNote(eq(id), contains("CONTAIN_AND_BLOCK_IP"), eq("analyst"));
    }
}
