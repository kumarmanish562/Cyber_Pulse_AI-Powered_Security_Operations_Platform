package cyberpulse.audit.controller;

import cyberpulse.audit.dto.AuditLogResponse;
import cyberpulse.audit.event.AuditAction;
import cyberpulse.audit.service.AuditLogService;
import cyberpulse.common.web.CsvExportUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/audit-logs")
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

    @GetMapping("/export")
    @PreAuthorize("hasAuthority('AUDIT_READ')")
    public ResponseEntity<?> exportAuditLogs(
            @RequestParam(defaultValue = "csv") String format
    ) {
        List<AuditLogResponse> rows = auditLogService
                .findAll(PageRequest.of(0, 1000))
                .getContent();

        if ("json".equalsIgnoreCase(format)) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"audit-logs-export.json\"")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(rows);
        }

        String csv = CsvExportUtil.toCsv(
                List.of("id", "userId", "action", "entityType", "entityId", "ipAddress", "createdAt", "details"),
                rows,
                r -> List.of(
                        r.id(),
                        r.userId() != null ? r.userId() : "",
                        r.action(),
                        r.entityType() != null ? r.entityType() : "",
                        r.entityId() != null ? r.entityId() : "",
                        r.ipAddress() != null ? r.ipAddress() : "",
                        r.createdAt(),
                        r.details() != null ? r.details() : ""
                )
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"audit-logs-export.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
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