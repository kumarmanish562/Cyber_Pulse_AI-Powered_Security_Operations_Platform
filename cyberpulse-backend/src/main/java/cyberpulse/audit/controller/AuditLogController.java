package cyberpulse.audit.controller;

import cyberpulse.audit.dto.AuditLogResponse;
import cyberpulse.audit.event.AuditAction;
import cyberpulse.audit.service.AuditLogService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;


    @GetMapping
    @PreAuthorize("hasAuthority('AUDIT_READ')")
    public Page<AuditLogResponse> getAuditLogs(
            Pageable pageable
    ) {

        return auditLogService.findAll(
                pageable
        );
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('AUDIT_READ')")
    public AuditLogResponse getAuditLog(
            @PathVariable UUID id
    ) {

        return auditLogService.findById(
                id
        );
    }


    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('AUDIT_READ')")
    public Page<AuditLogResponse> getByUser(
            @PathVariable UUID userId,
            Pageable pageable
    ) {

        return auditLogService.findByUser(
                userId,
                pageable
        );
    }


    @GetMapping("/action/{action}")
    @PreAuthorize("hasAuthority('AUDIT_READ')")
    public Page<AuditLogResponse> getByAction(
            @PathVariable AuditAction action,
            Pageable pageable
    ) {

        return auditLogService.findByAction(
                action,
                pageable
        );
    }


    @GetMapping("/entity/{entityType}/{entityId}")
    @PreAuthorize("hasAuthority('AUDIT_READ')")
    public Page<AuditLogResponse> getByEntity(
            @PathVariable String entityType,
            @PathVariable UUID entityId,
            Pageable pageable
    ) {

        return auditLogService.findByEntity(
                entityType,
                entityId,
                pageable
        );
    }
}