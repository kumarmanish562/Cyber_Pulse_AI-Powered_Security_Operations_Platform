package cyberpulse.playbook.controller;

import cyberpulse.playbook.dto.ExecutePlaybookRequest;
import cyberpulse.playbook.dto.ExecutePlaybookResponse;
import cyberpulse.playbook.service.PlaybookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/playbooks")
@RequiredArgsConstructor
public class PlaybookController {

    private final PlaybookService playbookService;

    @PostMapping("/incidents/{incidentId}/execute")
    @PreAuthorize("hasAuthority('INCIDENT_UPDATE')")
    public ExecutePlaybookResponse execute(
            @PathVariable UUID incidentId,
            @Valid @RequestBody ExecutePlaybookRequest request,
            Authentication authentication
    ) {
        return playbookService.execute(incidentId, request.action(), authentication.getName());
    }
}
