package cyberpulse.user.service;

import cyberpulse.auth.dto.UserResponse;
import cyberpulse.user.entity.User;
import cyberpulse.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(
            UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {

        return userRepository
                .findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private UserResponse toResponse(
            User user
    ) {

        List<String> roles =
                user.getRoles()
                        .stream()
                        .map(userRole ->
                                userRole.getRole().getName()
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