package cyberpulse.auth;

import org.junit.jupiter.api.Test;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

class AuthServiceTest {

    private final PasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    @Test
    void passwordShouldBeHashed() {

        String password =
                "Password@123";

        String hash =
                passwordEncoder.encode(password);

        assertThat(hash)
                .isNotEqualTo(password);

        assertThat(
                passwordEncoder.matches(
                        password,
                        hash
                )
        ).isTrue();
    }

    @Test
    void wrongPasswordShouldFail() {

        String password =
                "Password@123";

        String hash =
                passwordEncoder.encode(password);

        assertThat(
                passwordEncoder.matches(
                        "WrongPassword",
                        hash
                )
        ).isFalse();
    }
}