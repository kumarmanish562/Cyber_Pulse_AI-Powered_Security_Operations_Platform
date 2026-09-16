package cyberpulse.auth.security;

import cyberpulse.user.entity.User;
import cyberpulse.user.entity.UserRole;
import cyberpulse.user.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CustomUserDetailsService
        implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(
            UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(
            String username
    ) {

        User user = userRepository
                .findByUsernameIgnoreCase(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found"
                        )
                );

        List<GrantedAuthority> authorities =
                user.getRoles()
                        .stream()
                        .map(UserRole::getRole)
                        .map(role ->
                                new SimpleGrantedAuthority(
                                        "ROLE_" + role.getName()
                                )
                        )
                        .map(authority ->
                                (GrantedAuthority) authority
                        )
                        .toList();

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPasswordHash())
                .authorities(authorities)
                .disabled(!user.isEnabled())
                .accountLocked(user.isLocked())
                .build();
    }
}