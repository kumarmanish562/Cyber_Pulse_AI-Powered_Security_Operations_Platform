package cyberpulse.ai.controller;

import cyberpulse.ai.dto.AiTriageResponse;
import cyberpulse.ai.service.AiTriageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/incidents")
@RequiredArgsConstructor
public class AiTriageController {

    private final AiTriageService aiTriageService;

    @PostMapping("/{id}/ai-triage")
    @PreAuthorize("hasAuthority('INCIDENT_READ')")
    public AiTriageResponse triageIncident(@PathVariable UUID id) {
        return aiTriageService.triageIncident(id);
    }
}
