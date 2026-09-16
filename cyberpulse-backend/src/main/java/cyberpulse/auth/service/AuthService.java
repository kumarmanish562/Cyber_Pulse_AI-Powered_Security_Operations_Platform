package cyberpulse.auth.service;


import cyberpulse.auth.dto.AuthResponse;
import cyberpulse.auth.dto.LoginRequest;
import cyberpulse.auth.dto.RegisterRequest;
import cyberpulse.auth.dto.UserResponse;
import cyberpulse.auth.entity.Role;
import cyberpulse.auth.repository.RoleRepository;
import cyberpulse.auth.security.JwtService;
import cyberpulse.user.entity.User;
import cyberpulse.user.repository.UserRepository;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class AuthService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final UserDetailsService userDetailsService;

    private final JwtService jwtService;

    private final RefreshTokenService refreshTokenService;

    public AuthService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            UserDetailsService userDetailsService,
            JwtService jwtService,
            RefreshTokenService refreshTokenService
    ) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional
    public UserResponse register(
            RegisterRequest request
    ) {

        String username =
                request.username().trim();

        String email =
                request.email().trim().toLowerCase();

        if (
                userRepository
                        .existsByUsernameIgnoreCase(username)
        ) {

            throw new IllegalArgumentException(
                    "Username already exists"
            );
        }

        if (
                userRepository
                        .existsByEmailIgnoreCase(email)
        ) {

            throw new IllegalArgumentException(
                    "Email already exists"
            );
        }

        cyberpulse.user.entity.User user = new User();

        user.setUsername(username);

        user.setEmail(email);

        user.setPasswordHash(
                passwordEncoder.encode(
                        request.password()
                )
        );

        user.setEnabled(true);

        user.setLocked(false);

        user = userRepository.save(user);

        Role viewerRole =
                roleRepository
                        .findByName("VIEWER")
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "VIEWER role not configured"
                                )
                        );

        user.addRole(viewerRole);

        userRepository.save(user);

        return toUserResponse(user);
    }

    @Transactional
    public AuthResponse login(
            LoginRequest request
    ) {

        User user =
                findUser(request.usernameOrEmail());

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                user.getUsername(),
                                request.password()
                        )
                );

        UserDetails userDetails =
                (UserDetails) authentication.getPrincipal();

        String accessToken =
                jwtService.generateAccessToken(
                        userDetails
                );

        String refreshToken =
                refreshTokenService.createRefreshToken(
                        user
                );

        user.setLastLoginAt(
                Instant.now()
        );

        userRepository.save(user);

        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                jwtService.getAccessTokenExpiration(),
                toUserResponse(user)
        );
    }

    @Transactional
    public AuthResponse refresh(
            String rawRefreshToken
    ) {

        var refreshToken =
                refreshTokenService.validate(
                        rawRefreshToken
                );

        User user =
                refreshToken.getUser();

        if (!user.isEnabled()) {

            throw new IllegalStateException(
                    "User account is disabled"
            );
        }

        if (user.isLocked()) {

            throw new IllegalStateException(
                    "User account is locked"
            );
        }

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(
                        user.getUsername()
                );

        String newAccessToken =
                jwtService.generateAccessToken(
                        userDetails
                );

        String newRefreshToken =
                refreshTokenService.rotate(
                        rawRefreshToken
                );

        return new AuthResponse(
                newAccessToken,
                newRefreshToken,
                "Bearer",
                jwtService.getAccessTokenExpiration(),
                toUserResponse(user)
        );
    }

    @Transactional
    public void logout(
            String refreshToken
    ) {

        refreshTokenService.revoke(
                refreshToken
        );
    }

    @Transactional(readOnly = true)
    public UserResponse currentUser(
            String username
    ) {

        User user =
                userRepository
                        .findByUsernameIgnoreCase(username)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "User not found"
                                )
                        );

        return toUserResponse(user);
    }

    private User findUser(
            String usernameOrEmail
    ) {

        return userRepository
                .findByUsernameIgnoreCase(
                        usernameOrEmail
                )
                .orElseGet(() ->
                        userRepository
                                .findByEmailIgnoreCase(
                                        usernameOrEmail
                                )
                                .orElseThrow(() ->
                                        new IllegalArgumentException(
                                                "Invalid credentials"
                                        )
                                )
                );
    }

    private UserResponse toUserResponse(
            User user
    ) {

        List<String> roles =
                user.getRoles()
                        .stream()
                        .map(userRole ->
                                userRole
                                        .getRole()
                                        .getName()
                        )
                        .toList();

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                roles
        );
    }
}