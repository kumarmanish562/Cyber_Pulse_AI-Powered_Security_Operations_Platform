package cyberpulse.auth.repository;

import cyberpulse.auth.entity.RolePermission;
import cyberpulse.auth.entity.RolePermissionId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RolePermissionRepository
        extends JpaRepository<
                RolePermission,
                RolePermissionId
                > {

    List<RolePermission> findByRoleId(
            UUID roleId
    );

    boolean existsByRoleIdAndPermissionId(
            UUID roleId,
            UUID permissionId
    );
}