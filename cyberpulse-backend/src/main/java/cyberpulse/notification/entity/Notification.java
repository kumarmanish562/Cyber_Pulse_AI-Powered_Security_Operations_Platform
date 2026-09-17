package cyberpulse.notification.entity;

import cyberpulse.user.entity.User;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    @Column(
            nullable = false,
            length = 200
    )
    private String title;

    @Column(
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String message;

    @Column(
            nullable = false,
            length = 50
    )
    private String type;

    @Column(
            name = "entity_type",
            length = 100
    )
    private String entityType;

    @Column(name = "entity_id")
    private UUID entityId;

    @Column(nullable = false)
    private boolean read = false;

    @Column(
            name = "created_at",
            nullable = false
    )
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {

        createdAt = Instant.now();
    }
}