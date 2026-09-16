package cyberpulse.user.entity;


import cyberpulse.auth.entity.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(
            strategy = GenerationType.UUID
    )
    private UUID id;

    @Column(
            nullable = false,
            length = 50
    )
    private String username;

    @Column(
            nullable = false,
            length = 255
    )
    private String email;

    @Column(
            name = "password_hash",
            nullable = false
    )
    private String passwordHash;

    @Column(
            nullable = false
    )
    private boolean enabled = true;

    @Column(
            nullable = false
    )
    private boolean locked = false;

    @Column(
            name = "created_at",
            nullable = false
    )
    private Instant createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private Instant updatedAt;

    @Column(
            name = "last_login_at"
    )
    private Instant lastLoginAt;

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<UserRole> roles = new HashSet<>();

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

    public void addRole(Role role){

        UserRole userRole = new UserRole();

        UserRoleId userRoleId = new UserRoleId(
                this.id,
                role.getId()
        );

        userRole.setId(userRoleId);
        userRole.setUser(this);
        userRole.setRole(role);

        roles.add(userRole);
    }
}
