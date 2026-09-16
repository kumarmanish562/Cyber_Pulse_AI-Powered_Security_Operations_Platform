package cyberpulse.user.service;

import cyberpulse.auth.entity.Role;
import cyberpulse.auth.repository.RoleRepository;
import cyberpulse.common.enums.RoleName;
import cyberpulse.user.entity.User;
import cyberpulse.user.entity.UserRole;
import cyberpulse.user.entity.UserRoleId;
import cyberpulse.user.repository.UserRepository;
import cyberpulse.user.repository.UserRoleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserAuthorizationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    public UserAuthorizationService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserRoleRepository userRoleRepository
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Transactional
    public void assignRole(
            UUID userId,
            String roleName
    ) {

        User user = userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "User not found"
                        )
                );

        String normalizedRoleName =
                roleName.trim();

        Role role = roleRepository
                .findByNameIgnoreCase(normalizedRoleName)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Role not found"
                        )
                );

        UserRoleId id =
                new UserRoleId(
                        user.getId(),
                        role.getId()
                );

        if (userRoleRepository.existsById(id)) {
            return;
        }

        UserRole userRole = new UserRole();

        userRole.setId(id);
        userRole.setUser(user);
        userRole.setRole(role);

        userRoleRepository.save(userRole);
    }

    @Transactional
    public void removeRole(
            UUID userId,
            String roleName
    ) {

        User user = userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "User not found"
                        )
                );

        String normalizedRoleName =
                roleName.trim();

        Role role = roleRepository
                .findByNameIgnoreCase(normalizedRoleName)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Role not found"
                        )
                );

        if (RoleName.ADMIN.equalsIgnoreCase(role.getName())) {

            long adminCount =
                    userRoleRepository
                            .countActiveUsersWithRole(
                                    RoleName.ADMIN
                            );

            if (adminCount <= 1) {
                throw new IllegalStateException(
                        "Cannot remove the last active ADMIN"
                );
            }
        }


        UserRoleId id =
                new UserRoleId(
                        user.getId(),
                        role.getId()
                );

        if (!userRoleRepository.existsById(id)) {
            return;
        }

        userRoleRepository.deleteById(id);
    }
}