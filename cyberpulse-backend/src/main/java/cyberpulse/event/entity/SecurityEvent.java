package cyberpulse.event.entity;



import cyberpulse.common.enums.EventType;
import cyberpulse.common.enums.Severity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.InetAddress;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "security_events")
@Getter
@Setter
@NoArgsConstructor
public class SecurityEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "event_type",
            nullable = false,
            length = 50
    )
    private EventType eventType;

    @Column(name = "source_ip")
    private InetAddress sourceIp;

    @Column(length = 100)
    private String username;

    @Column(length = 100)
    private String service;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    private Severity severity;

    @Column(
            name = "event_time",
            nullable = false
    )
    private Instant eventTime;

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
