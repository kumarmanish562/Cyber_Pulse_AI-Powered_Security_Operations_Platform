package cyberpulse.audit.entity;

import cyberpulse.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.net.InetAddress;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    // =========================================================
    // USER
    // =========================================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    // =========================================================
    // AUDIT INFORMATION
    // =========================================================

    @Column(
            nullable = false,
            length = 100
    )
    private String action;


    @Column(
            name = "entity_type",
            length = 100
    )
    private String entityType;


    @Column(name = "entity_id")
    private UUID entityId;


    // =========================================================
    // REQUEST INFORMATION
    // =========================================================

    @Column(name = "ip_address")
    private InetAddress ipAddress;


    @Column(
            name = "user_agent",
            length = 1000
    )
    private String userAgent;


    // =========================================================
    // DETAILS
    // =========================================================

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(
            columnDefinition = "jsonb"
    )
    private String details;


    // =========================================================
    // TIMESTAMP
    // =========================================================

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private Instant createdAt = Instant.now();


    // =========================================================
    // JPA SAFETY
    // =========================================================

    @PrePersist
    protected void onCreate() {

        if (createdAt == null) {

            createdAt = Instant.now();
        }
    }
}