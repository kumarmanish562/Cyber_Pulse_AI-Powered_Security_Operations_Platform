package cyberpulse.report.service;

import cyberpulse.blocklist.service.BlocklistService;
import cyberpulse.detection.repository.ThreatDetectionRepository;
import cyberpulse.event.repository.SecurityEventRepository;
import cyberpulse.report.dto.ShiftHandoverReport;
import cyberpulse.soc.dto.SocPostureResponse;
import cyberpulse.soc.service.SocPostureService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShiftHandoverService {

    private static final int WINDOW_HOURS = 24;

    private final SecurityEventRepository eventRepository;
    private final ThreatDetectionRepository detectionRepository;
    private final BlocklistService blocklistService;
    private final SocPostureService socPostureService;

    @Transactional(readOnly = true)
    public ShiftHandoverReport generate24HourReport() {
        Instant now = Instant.now();
        Instant since = now.minus(WINDOW_HOURS, ChronoUnit.HOURS);

        long events24h = eventRepository.findAll().stream()
                .filter(e -> e.getEventTime() != null && !e.getEventTime().isBefore(since))
                .count();

        long detections24h = detectionRepository.findAll().stream()
                .filter(d -> d.getCreatedAt() != null && !d.getCreatedAt().isBefore(since))
                .count();

        SocPostureResponse posture = socPostureService.getPosture();
        long activeBlocked = blocklistService.getActiveBlocklist().size();

        double compliance = posture.openIncidents() == 0
                ? 100.0
                : Math.round(((double) (posture.openIncidents() - posture.slaBreachedCount())
                        / posture.openIncidents()) * 1000.0) / 10.0;

        List<String> topMitre = posture.mitreTechniques().stream()
                .limit(5)
                .map(m -> m.techniqueId() + " (" + m.techniqueName() + "): " + m.detectionCount())
                .toList();

        StringBuilder md = new StringBuilder();
        md.append("# CyberPulse SOC Shift Handover Report\n\n");
        md.append("- **Generated At:** ").append(now).append("\n");
        md.append("- **Window:** Last ").append(WINDOW_HOURS).append(" hours\n\n");
        md.append("## Operational Metrics\n");
        md.append("- **Events Ingested (24h):** ").append(events24h).append("\n");
        md.append("- **Threat Detections (24h):** ").append(detections24h).append("\n");
        md.append("- **Open / Investigating Incidents:** ").append(posture.openIncidents()).append("\n");
        md.append("- **SLA Breached Incidents:** ").append(posture.slaBreachedCount()).append("\n");
        md.append("- **SLA Compliance Rate:** ").append(compliance).append("%\n");
        md.append("- **Active Blocked IPs:** ").append(activeBlocked).append("\n\n");
        md.append("## Top MITRE ATT&CK Techniques\n");
        if (topMitre.isEmpty()) {
            md.append("- None observed\n");
        } else {
            topMitre.forEach(item -> md.append("- ").append(item).append("\n"));
        }

        return new ShiftHandoverReport(
                now,
                WINDOW_HOURS,
                events24h,
                detections24h,
                posture.openIncidents(),
                posture.slaBreachedCount(),
                compliance,
                activeBlocked,
                topMitre,
                md.toString()
        );
    }
}
