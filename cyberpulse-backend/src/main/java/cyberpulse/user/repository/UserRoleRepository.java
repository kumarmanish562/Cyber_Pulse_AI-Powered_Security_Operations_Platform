package cyberpulse.user.repository;

import cyberpulse.user.entity.UserRole;
import cyberpulse.user.entity.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface UserRoleRepository
        extends JpaRepository<UserRole, UserRoleId> {

    List<UserRole> findByUserId(UUID userId);

    boolean existsByUserIdAndRoleId(
            UUID userId,
            UUID roleId
    );

    long countByRoleName(String roleName);

    void deleteByUserIdAndRoleId(
            UUID userId,
            UUID roleId
    );

    @Query("""
        SELECT COUNT(ur)
        FROM UserRole ur
        WHERE ur.role.name = :roleName
        AND ur.user.enabled = true
        AND ur.user.locked = false
    """)
    long countActiveUsersWithRole(
            String roleName
    );
}
