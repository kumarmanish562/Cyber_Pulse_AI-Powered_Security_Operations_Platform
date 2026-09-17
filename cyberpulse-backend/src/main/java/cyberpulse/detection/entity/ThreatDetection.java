package cyberpulse.detection.entity;

import cyberpulse.event.entity.SecurityEvent;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "threat_detections")
@Getter
@Setter
@NoArgsConstructor
public class ThreatDetection {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "rule_id",
            nullable = false
    )
    private DetectionRule rule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "event_id",
            nullable = false
    )
    private SecurityEvent event;

    @Column(
            name = "threat_type",
            nullable = false,
            length = 100
    )
    private String threatType;

    @Column(
            name = "confidence_score",
            precision = 5,
            scale = 2
    )
    private BigDecimal confidenceScore;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(
            columnDefinition = "jsonb"
    )
    private Map<String, Object> details;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}