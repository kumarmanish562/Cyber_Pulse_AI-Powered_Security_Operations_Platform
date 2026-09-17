package cyberpulse.detection.entity;


import cyberpulse.common.enums.DetectionRuleType;
import cyberpulse.common.enums.Severity;
import cyberpulse.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "detection_rules",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_detection_rules_name",
                columnNames = "name"
        )
)
@Getter
@Setter
@NoArgsConstructor
public class DetectionRule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(
            nullable = false,
            length = 100
    )
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "rule_type",
            nullable = false,
            length = 50
    )
    private DetectionRuleType ruleType;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    private Severity severity;

    @Column(name = "threshold_value")
    private Integer thresholdValue;

    @Column(name = "time_window_seconds")
    private Integer timeWindowSeconds;

    @Column(nullable = false)
    private boolean enabled = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private Instant createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {

        Instant now = Instant.now();

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {

        updatedAt = Instant.now();
    }
}