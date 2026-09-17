package cyberpulse.risk.event;

import cyberpulse.detection.event.ThreatDetectedEvent;
import cyberpulse.risk.engine.RiskEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class ThreatDetectionRiskListener {

    private final RiskEngine riskEngine;

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handle(
            ThreatDetectedEvent event
    ) {

        log.info(
                "Threat detected. Starting risk assessment detectionId={}",
                event.threatDetectionId()
        );

        try {

            riskEngine.assessThreat(
                    event.threatDetectionId()
            );

        } catch (Exception ex) {

            log.error(
                    "Risk assessment failed detectionId={}",
                    event.threatDetectionId(),
                    ex
            );
        }
    }
}