package cyberpulse.user.repository;

import cyberpulse.user.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository
        extends JpaRepository<User, UUID> {

    Optional<User> findByUsernameIgnoreCase(
            String username
    );

    Optional<User> findByEmailIgnoreCase(
            String email
    );

    boolean existsByUsernameIgnoreCase(
            String username
    );

    boolean existsByEmailIgnoreCase(
            String email
    );


    @Query("""
        SELECT DISTINCT u
        FROM User u
        JOIN u.roles ur
        JOIN ur.role r
        JOIN r.rolePermissions rp
        JOIN rp.permission p
        WHERE p.name = 'INCIDENT_READ'
          AND u.enabled = true
          AND u.locked = false
    """)
    List<User> findUsersWithIncidentReadPermission();
}