package cyberpulse.incident.engine;

import cyberpulse.incident.dto.CreateIncidentRequest;
import cyberpulse.incident.service.IncidentService;
import cyberpulse.risk.entity.RiskAssessment;
import cyberpulse.risk.repository.RiskAssessmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class IncidentEngine {

    private final RiskAssessmentRepository
            riskAssessmentRepository;

    private final IncidentService
            incidentService;

    @Transactional
    public void evaluateRisk(
            UUID riskAssessmentId
    ) {

        RiskAssessment assessment =
                riskAssessmentRepository
                        .findById(
                                riskAssessmentId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Risk assessment not found"
                                )
                        );

        switch (assessment.getSeverity()) {

            case HIGH, CRITICAL ->

                    createIncident(assessment);

            case LOW, MEDIUM ->

                    log.debug(
                            "Risk assessment does not require incident creation. id={}",
                            riskAssessmentId
                    );
        }
    }

    private void createIncident(
            RiskAssessment assessment
    ) {

        CreateIncidentRequest request =
                new CreateIncidentRequest(

                        assessment.getId(),

                        buildTitle(assessment),

                        buildDescription(assessment)
                );

        incidentService.createIncident(
                request
        );

        log.info(
                "Incident created for risk assessment {}",
                assessment.getId()
        );
    }

    private String buildTitle(
            RiskAssessment assessment
    ) {

        return "Security Incident - "
                + assessment
                .getThreatDetection()
                .getThreatType();
    }

    private String buildDescription(
            RiskAssessment assessment
    ) {

        return "Incident automatically created from "
                + "risk assessment "
                + assessment.getId()
                + ". Risk score: "
                + assessment.getRiskScore()
                + ", severity: "
                + assessment.getSeverity();
    }
}