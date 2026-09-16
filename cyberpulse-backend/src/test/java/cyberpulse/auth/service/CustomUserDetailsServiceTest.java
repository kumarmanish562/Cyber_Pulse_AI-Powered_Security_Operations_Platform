package cyberpulse.auth.service;

import cyberpulse.auth.entity.Permission;
import cyberpulse.auth.entity.Role;
import cyberpulse.auth.entity.RolePermission;
import cyberpulse.auth.entity.RolePermissionId;
import cyberpulse.auth.security.CustomUserDetailsService;
import cyberpulse.user.entity.User;
import cyberpulse.user.entity.UserRole;
import cyberpulse.user.entity.UserRoleId;
import cyberpulse.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;
import java.util.UUID;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService service;

    @Test
    void shouldLoadRolesAndPermissions() {

        UUID userId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();
        UUID permissionId = UUID.randomUUID();

        Permission permission = new Permission();
        permission.setId(permissionId);
        permission.setName("EVENT_READ");

        Role role = new Role(
                "SECURITY_ANALYST",
                "Security analyst"
        );

        role.setId(roleId);

        RolePermission rolePermission =
                new RolePermission();

        rolePermission.setId(
                new RolePermissionId(
                        roleId,
                        permissionId
                )
        );

        rolePermission.setRole(role);
        rolePermission.setPermission(permission);

        role.getRolePermissions()
                .add(rolePermission);

        User user = new User();
        user.setId(userId);
        user.setUsername("analyst1");
        user.setPasswordHash("hashed-password");

        UserRole userRole =
                new UserRole();

        userRole.setId(
                new UserRoleId(
                        userId,
                        roleId
                )
        );

        userRole.setUser(user);
        userRole.setRole(role);

        user.getRoles()
                .add(userRole);

        when(
                userRepository
                        .findByUsernameIgnoreCase("analyst1")
        )
                .thenReturn(Optional.of(user));

        UserDetails result =
                service.loadUserByUsername("analyst1");

        assertThat(
                result.getAuthorities()
        )
                .extracting("authority")
                .contains(
                        "ROLE_SECURITY_ANALYST",
                        "EVENT_READ"
                );
    }
}