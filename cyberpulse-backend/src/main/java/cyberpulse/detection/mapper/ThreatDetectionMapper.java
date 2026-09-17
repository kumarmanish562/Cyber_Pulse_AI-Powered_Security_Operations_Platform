package cyberpulse.detection.mapper;

import cyberpulse.detection.dto.ThreatDetectionResponse;
import cyberpulse.detection.entity.ThreatDetection;
import org.springframework.stereotype.Component;

@Component
public class ThreatDetectionMapper {

    public ThreatDetectionResponse toResponse(
            ThreatDetection detection
    ) {

        return new ThreatDetectionResponse(
                detection.getId(),
                detection.getRule().getId(),
                detection.getRule().getName(),
                detection.getEvent().getId(),
                detection.getThreatType(),
                detection.getConfidenceScore(),
                detection.getDetails(),
                detection.getCreatedAt()
        );
    }
}