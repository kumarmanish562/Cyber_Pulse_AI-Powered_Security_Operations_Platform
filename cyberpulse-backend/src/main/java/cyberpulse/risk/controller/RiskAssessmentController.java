package cyberpulse.risk.controller;

import cyberpulse.risk.dto.RiskAssessmentResponse;
import cyberpulse.risk.entity.RiskSeverity;
import cyberpulse.risk.service.RiskAssessmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;



@RestController
@RequestMapping("/api/risk-assessments")
@RequiredArgsConstructor
public class RiskAssessmentController {

    private final RiskAssessmentService
            riskAssessmentService;

    @GetMapping
    @PreAuthorize("hasAuthority('RISK_READ')")
    public Page<RiskAssessmentResponse> getAll(
            @PageableDefault(size = 20)
            Pageable pageable
    ) {

        return riskAssessmentService.getAll(
                pageable
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('RISK_READ')")
    public RiskAssessmentResponse getById(
            @PathVariable UUID id
    ) {

        return riskAssessmentService.getById(id);
    }

    @GetMapping("/detection/{threatDetectionId}")
    @PreAuthorize("hasAuthority('RISK_READ')")
    public RiskAssessmentResponse
    getByThreatDetection(
            @PathVariable UUID threatDetectionId
    ) {

        return riskAssessmentService
                .getByThreatDetectionId(
                        threatDetectionId
                );
    }

    @GetMapping("/severity/{severity}")
    @PreAuthorize("hasAuthority('RISK_READ')")
    public Page<RiskAssessmentResponse>
    getBySeverity(
            @PathVariable RiskSeverity severity,
            @PageableDefault(size = 20)
            Pageable pageable
    ) {

        return riskAssessmentService
                .getBySeverity(
                        severity,
                        pageable
                );
    }
}