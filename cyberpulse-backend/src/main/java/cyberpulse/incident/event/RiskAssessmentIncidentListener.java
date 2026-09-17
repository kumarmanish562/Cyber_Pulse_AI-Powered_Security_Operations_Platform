package cyberpulse.incident.event;

import cyberpulse.incident.engine.IncidentEngine;
import cyberpulse.risk.event.RiskAssessmentCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class RiskAssessmentIncidentListener {

    private final IncidentEngine incidentEngine;

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handle(
            RiskAssessmentCreatedEvent event
    ) {

        log.info(
                "Evaluating incident creation for risk assessment {}",
                event.riskAssessmentId()
        );

        incidentEngine.evaluateRisk(
                event.riskAssessmentId()
        );
    }
}