package cyberpulse.detection.controller;

import cyberpulse.detection.dto.CreateDetectionRuleRequest;
import cyberpulse.detection.dto.DetectionRuleResponse;
import cyberpulse.detection.dto.UpdateDetectionRuleRequest;
import cyberpulse.detection.service.DetectionRuleService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rules")
public class DetectionRuleController {

    private final DetectionRuleService ruleService;

    public DetectionRuleController(
            DetectionRuleService ruleService
    ) {
        this.ruleService = ruleService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('RULE_WRITE')")
    public ResponseEntity<DetectionRuleResponse> create(
            @Valid @RequestBody
            CreateDetectionRuleRequest request,
            Authentication authentication
    ) {

        UUID userId = UUID.fromString(
                authentication.getName()
        );

        DetectionRuleResponse response =
                ruleService.create(
                        request,
                        authentication.getName()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('RULE_READ')")
    public ResponseEntity<List<DetectionRuleResponse>> getAll() {

        return ResponseEntity.ok(
                ruleService.getAll()
        );
    }

    @GetMapping("/enabled")
    @PreAuthorize("hasAuthority('RULE_READ')")
    public ResponseEntity<List<DetectionRuleResponse>> getEnabled() {

        return ResponseEntity.ok(
                ruleService.getEnabled()
        );
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('RULE_WRITE')")
    public ResponseEntity<DetectionRuleResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody
            UpdateDetectionRuleRequest request
    ) {

        return ResponseEntity.ok(
                ruleService.update(
                        id,
                        request
                )
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('RULE_DELETE')")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id
    ) {

        ruleService.delete(id);

        return ResponseEntity.noContent()
                .build();
    }
}