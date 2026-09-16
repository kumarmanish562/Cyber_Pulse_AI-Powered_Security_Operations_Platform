package cyberpulse.event.mapper;

import cyberpulse.event.dto.CreateSecurityEventRequest;
import cyberpulse.event.dto.SecurityEventResponse;
import cyberpulse.event.entity.SecurityEvent;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

@Component
public class SecurityEventMapper {

    public SecurityEvent toEntity(
            CreateSecurityEventRequest request
    ) {

        SecurityEvent event = new SecurityEvent();

        event.setEventType(request.eventType());

        if (request.sourceIp() != null &&
                !request.sourceIp().isBlank()) {

            try {
                event.setSourceIp(
                        InetAddress.getByName(
                                request.sourceIp().trim()
                        )
                );
            } catch (Exception exception) {
                throw new IllegalArgumentException(
                        "Invalid source IP address"
                );
            }
        }

        event.setUsername(normalize(request.username()));
        event.setService(normalize(request.service()));
        event.setMessage(normalize(request.message()));
        event.setSeverity(request.severity());
        event.setEventTime(request.eventTime());

        return event;
    }

    public SecurityEventResponse toResponse(
            SecurityEvent event
    ) {

        String sourceIp = event.getSourceIp() != null
                ? event.getSourceIp().getHostAddress()
                : null;

        return new SecurityEventResponse(
                event.getId(),
                event.getEventType(),
                sourceIp,
                event.getUsername(),
                event.getService(),
                event.getMessage(),
                event.getSeverity(),
                event.getEventTime(),
                event.getCreatedAt()
        );
    }

    private String normalize(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}