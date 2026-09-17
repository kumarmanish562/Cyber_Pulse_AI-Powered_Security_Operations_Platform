package cyberpulse.risk.entity;


import cyberpulse.detection.entity.ThreatDetection;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(
        name = "risk_assessments",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_risk_assessment_detection",
                        columnNames = "threat_detection_id"
                )
        },
        indexes = {
                @Index(
                        name = "idx_risk_assessments_severity",
                        columnList = "severity"
                ),
                @Index(
                        name = "idx_risk_assessments_assessed_at",
                        columnList = "assessed_at"
                ),
                @Index(
                        name = "idx_risk_assessments_score",
                        columnList = "risk_score"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class RiskAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "threat_detection_id",
            nullable = false,
            unique = true
    )
    private ThreatDetection threatDetection;

    @Column(
            name = "risk_score",
            nullable = false
    )
    private Integer riskScore;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    private RiskSeverity severity;

    @Column(
            name = "base_score",
            nullable = false
    )
    private Integer baseScore;

    @Column(
            name = "confidence_score",
            nullable = false
    )
    private Integer confidenceScore;

    @Column(
            name = "frequency_score",
            nullable = false
    )
    private Integer frequencyScore;

    @Column(
            name = "severity_score",
            nullable = false
    )
    private Integer severityScore;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(
            columnDefinition = "jsonb"
    )
    private Map<String, Object> factors;

    @Column(
            name = "assessed_at",
            nullable = false,
            updatable = false
    )
    private Instant assessedAt;

    @PrePersist
    protected void onCreate() {
        assessedAt = Instant.now();
    }
}
