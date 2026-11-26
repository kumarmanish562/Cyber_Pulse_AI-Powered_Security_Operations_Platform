package cyberpulse.soc.service;

import cyberpulse.detection.entity.ThreatDetection;
import cyberpulse.detection.repository.ThreatDetectionRepository;
import cyberpulse.incident.entity.Incident;
import cyberpulse.incident.entity.IncidentSeverity;
import cyberpulse.incident.entity.IncidentStatus;
import cyberpulse.incident.repository.IncidentRepository;
import cyberpulse.soc.dto.SocPostureResponse;
import cyberpulse.soc.dto.SocPostureResponse.MitreTechniqueSummary;
import cyberpulse.soc.dto.SocPostureResponse.SlaIncidentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SocPostureService {

    private final IncidentRepository incidentRepository;
    private final ThreatDetectionRepository detectionRepository;
    private final MitreMappingService mitreMappingService;

    @Transactional(readOnly = true)
    public SocPostureResponse getPosture() {
        Instant now = Instant.now();

        List<Incident> activeIncidents = incidentRepository.findAll().stream()
                .filter(i -> i.getStatus() == IncidentStatus.OPEN || i.getStatus() == IncidentStatus.INVESTIGATING)
                .toList();

        List<SlaIncidentStatus> slaList = activeIncidents.stream()
                .map(i -> {
                    long target = slaMinutesFor(i.getSeverity());
                    long elapsed = Math.max(0L, Duration.between(i.getCreatedAt(), now).toMinutes());
                    boolean breached = elapsed > target;
                    return new SlaIncidentStatus(
                            i.getId(),
                            i.getIncidentNumber(),
                            i.getTitle(),
                            i.getSeverity().name(),
                            i.getStatus().name(),
                            elapsed,
                            target,
                            breached
                    );
                })
                .sorted(Comparator.comparing(SlaIncidentStatus::breached).reversed()
                        .thenComparing(SlaIncidentStatus::elapsedMinutes, Comparator.reverseOrder()))
                .toList();

        long breachedCount = slaList.stream().filter(SlaIncidentStatus::breached).count();
        long atRiskCount = slaList.stream()
                .filter(s -> !s.breached() && s.elapsedMinutes() >= (long) (s.slaTargetMinutes() * 0.75))
                .count();

        Map<String, MitreAccumulator> mitreCounts = new LinkedHashMap<>();
        for (ThreatDetection detection : detectionRepository.findAll()) {
            var tech = mitreMappingService.resolve(detection.getThreatType());
            mitreCounts.compute(tech.techniqueId(), (k, existing) -> {
                if (existing == null) {
                    return new MitreAccumulator(tech, 1L);
                }
                existing.count++;
                return existing;
            });
        }

        List<MitreTechniqueSummary> mitreSummaries = mitreCounts.values().stream()
                .map(acc -> new MitreTechniqueSummary(
                        acc.tech.tacticId(),
                        acc.tech.tacticName(),
                        acc.tech.techniqueId(),
                        acc.tech.techniqueName(),
                        acc.count
                ))
                .sorted(Comparator.comparingLong(MitreTechniqueSummary::detectionCount).reversed())
                .toList();

        return new SocPostureResponse(
                activeIncidents.size(),
                breachedCount,
                atRiskCount,
                slaList.stream().limit(10).toList(),
                mitreSummaries
        );
    }

    public static long slaMinutesFor(IncidentSeverity severity) {
        if (severity == null) return 240L;
        return switch (severity) {
            case CRITICAL -> 15L;
            case HIGH -> 60L;
            case MEDIUM -> 240L;
            case LOW -> 1440L;
        };
    }

    private static final class MitreAccumulator {
        final MitreMappingService.MitreTechnique tech;
        long count;

        MitreAccumulator(MitreMappingService.MitreTechnique tech, long count) {
            this.tech = tech;
            this.count = count;
        }
    }
}
