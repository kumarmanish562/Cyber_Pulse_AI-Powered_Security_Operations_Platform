package cyberpulse.database;

import cyberpulse.auth.repository.RoleRepository;
import cyberpulse.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class DatabaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void databaseRepositoriesShouldLoad() {
        assertThat(userRepository).isNotNull();
        assertThat(roleRepository).isNotNull();
    }
}