package cyberpulse.risk.engine;

import cyberpulse.detection.entity.ThreatDetection;
import cyberpulse.detection.repository.ThreatDetectionRepository;
import cyberpulse.risk.service.RiskAssessmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RiskEngine {

    private final ThreatDetectionRepository
            threatDetectionRepository;

    private final RiskAssessmentService
            riskAssessmentService;

    public void assessThreat(
            UUID threatDetectionId
    ) {

        log.info(
                "Starting risk assessment detectionId={}",
                threatDetectionId
        );

        threatDetectionRepository
                .findById(threatDetectionId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Threat detection not found"
                        )
                );

        riskAssessmentService.assess(
                threatDetectionId
        );

        log.info(
                "Risk assessment completed detectionId={}",
                threatDetectionId
        );
    }
}