package cyberpulse.detection.service;

import cyberpulse.detection.dto.CreateDetectionRuleRequest;
import cyberpulse.detection.dto.DetectionRuleResponse;
import cyberpulse.detection.dto.UpdateDetectionRuleRequest;
import cyberpulse.detection.entity.DetectionRule;
import cyberpulse.detection.mapper.DetectionRuleMapper;
import cyberpulse.detection.repository.DetectionRuleRepository;
import cyberpulse.user.entity.User;
import cyberpulse.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class DetectionRuleService {

    private final DetectionRuleRepository ruleRepository;
    private final UserRepository userRepository;
    private final DetectionRuleMapper ruleMapper;

    public DetectionRuleService(
            DetectionRuleRepository ruleRepository,
            UserRepository userRepository,
            DetectionRuleMapper ruleMapper
    ) {
        this.ruleRepository = ruleRepository;
        this.userRepository = userRepository;
        this.ruleMapper = ruleMapper;
    }

    @Transactional
    public DetectionRuleResponse create(
            CreateDetectionRuleRequest request,
            String username
    ) {

        if (ruleRepository.existsByNameIgnoreCase(
                request.name().trim()
        )) {
            throw new IllegalArgumentException(
                    "Detection rule already exists"
            );
        }

        User user = userRepository
                .findByUsernameIgnoreCase(username)
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                "User not found"
                        )
                );

        validateRuleConfiguration(request);

        DetectionRule rule =
                ruleMapper.toEntity(request);

        rule.setCreatedBy(user);

        DetectionRule saved =
                ruleRepository.save(rule);

        return ruleMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<DetectionRuleResponse> getAll() {

        return ruleRepository.findAll()
                .stream()
                .map(ruleMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DetectionRuleResponse> getEnabled() {

        return ruleRepository.findByEnabledTrue()
                .stream()
                .map(ruleMapper::toResponse)
                .toList();
    }

    @Transactional
    public DetectionRuleResponse update(
            UUID ruleId,
            UpdateDetectionRuleRequest request
    ) {

        DetectionRule rule =
                findRule(ruleId);

        validateRuleConfiguration(
                rule.getRuleType(),
                request.thresholdValue(),
                request.timeWindowSeconds()
        );

        ruleMapper.updateEntity(
                rule,
                request
        );

        return ruleMapper.toResponse(
                ruleRepository.save(rule)
        );
    }

    @Transactional
    public void delete(UUID ruleId) {

        DetectionRule rule =
                findRule(ruleId);

        ruleRepository.delete(rule);
    }

    private DetectionRule findRule(UUID ruleId) {

        return ruleRepository.findById(ruleId)
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                "Detection rule not found: "
                                        + ruleId
                        )
                );
    }

    private void validateRuleConfiguration(
            CreateDetectionRuleRequest request
    ) {

        validateRuleConfiguration(
                request.ruleType(),
                request.thresholdValue(),
                request.timeWindowSeconds()
        );
    }

    private void validateRuleConfiguration(
            cyberpulse.common.enums.DetectionRuleType ruleType,
            Integer threshold,
            Integer window
    ) {

        if (ruleType == null) {
            throw new IllegalArgumentException(
                    "Rule type is required"
            );
        }

        if (ruleType ==
                cyberpulse.common.enums.DetectionRuleType.BRUTE_FORCE) {

            if (threshold == null || threshold < 2) {
                throw new IllegalArgumentException(
                        "BRUTE_FORCE threshold must be at least 2"
                );
            }

            if (window == null || window < 1) {
                throw new IllegalArgumentException(
                        "BRUTE_FORCE time window is required"
                );
            }
        }
    }
}