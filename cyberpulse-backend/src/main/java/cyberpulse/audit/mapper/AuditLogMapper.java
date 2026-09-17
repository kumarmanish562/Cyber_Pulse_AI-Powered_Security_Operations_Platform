package cyberpulse.audit.mapper;

import cyberpulse.audit.dto.AuditLogResponse;
import cyberpulse.audit.entity.AuditLog;

import org.springframework.stereotype.Component;

@Component
public class AuditLogMapper {

    public AuditLogResponse toResponse(
            AuditLog auditLog
    ) {

        return new AuditLogResponse(

                auditLog.getId(),

                auditLog.getUser() != null
                        ? auditLog.getUser().getId()
                        : null,

                auditLog.getAction(),

                auditLog.getEntityType(),

                auditLog.getEntityId(),

                auditLog.getIpAddress() != null
                        ? auditLog
                        .getIpAddress()
                        .getHostAddress()
                        : null,

                auditLog.getUserAgent(),

                auditLog.getDetails(),

                auditLog.getCreatedAt()
        );
    }
}