package cyberpulse.detection.service;

import cyberpulse.detection.dto.ThreatDetectionResponse;
import cyberpulse.detection.engine.DetectionRuleEvaluator;
import cyberpulse.detection.entity.DetectionRule;
import cyberpulse.detection.entity.ThreatDetection;
import cyberpulse.detection.event.ThreatDetectedEvent;
import cyberpulse.detection.mapper.ThreatDetectionMapper;
import cyberpulse.detection.repository.ThreatDetectionRepository;
import cyberpulse.event.entity.SecurityEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ThreatDetectionService {

    private final ThreatDetectionRepository
            detectionRepository;

    private final ThreatDetectionMapper
            detectionMapper;

    private final ApplicationEventPublisher
            applicationEventPublisher;

    public ThreatDetectionService(
            ThreatDetectionRepository detectionRepository,
            ThreatDetectionMapper detectionMapper, ApplicationEventPublisher applicationEventPublisher
    ) {
        this.detectionRepository = detectionRepository;
        this.detectionMapper = detectionMapper;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Transactional
    public Optional<ThreatDetectionResponse> createIfNotExists(
            SecurityEvent event,
            DetectionRule rule,
            DetectionRuleEvaluator.DetectionEvaluation evaluation
    ) {

        if (detectionRepository
                .existsByRuleIdAndEventId(
                        rule.getId(),
                        event.getId()
                )) {

            return Optional.empty();
        }

        ThreatDetection detection =
                new ThreatDetection();

        detection.setRule(rule);
        detection.setEvent(event);
        detection.setThreatType(
                evaluation.threatType()
        );
        detection.setConfidenceScore(
                evaluation.confidenceScore()
        );
        detection.setDetails(
                evaluation.details()
        );

        ThreatDetection saved =
                detectionRepository.save(
                        detection
                );

        applicationEventPublisher.publishEvent(
                new ThreatDetectedEvent(
                        saved.getId()
                )
        );

        return Optional.of(
                detectionMapper.toResponse(saved)
        );
    }

    @Transactional(readOnly = true)
    public List<ThreatDetectionResponse> getAll() {

        return detectionRepository
                .findAll()
                .stream()
                .map(detectionMapper::toResponse)
                .toList();
    }
}
