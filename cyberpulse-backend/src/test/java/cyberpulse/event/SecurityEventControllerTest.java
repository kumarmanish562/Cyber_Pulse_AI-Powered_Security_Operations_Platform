package cyberpulse.event;

import cyberpulse.event.controller.SecurityEventController;
import cyberpulse.event.service.SecurityEventService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(SecurityEventController.class)
@Import(SecurityEventControllerTest.TestSecurityConfig.class)
class SecurityEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SecurityEventService eventService;


    // ============================================================
    // 1. No authentication -> 401
    // ============================================================

    @Test
    void unauthenticatedUserShouldReceive401() throws Exception {

        mockMvc.perform(
                        get("/api/events")
                )
                .andExpect(
                        status().isUnauthorized()
                );
    }


    // ============================================================
    // 2. EVENT_READ authority -> 200
    // ============================================================

    @Test
    void viewerWithEventReadShouldReceive200() throws Exception {

        mockMvc.perform(
                        get("/api/events")
                                .with(
                                        user("viewer")
                                                .authorities(
                                                        new SimpleGrantedAuthority("ROLE_VIEWER"),
                                                        new SimpleGrantedAuthority("EVENT_READ")
                                                )
                                )
                )
                .andExpect(
                        status().isOk()
                );
    }


    // ============================================================
    // 3. No EVENT_READ authority -> 403
    // ============================================================

    @Test
    void userWithoutEventReadShouldReceive403() throws Exception {

        mockMvc.perform(
                        get("/api/events")
                                .with(
                                        user("viewer")
                                                .authorities(
                                                        new SimpleGrantedAuthority("ROLE_VIEWER")
                                                )
                                )
                )
                .andExpect(
                        status().isForbidden()
                );
    }


    // ============================================================
    // Test Security Configuration
    // ============================================================

    @TestConfiguration
    static class TestSecurityConfig {

        @Bean
        SecurityFilterChain testSecurityFilterChain(
                HttpSecurity http
        ) throws Exception {

            http
                    .csrf(csrf -> csrf.disable())

                    .authorizeHttpRequests(auth ->
                            auth
                                    .requestMatchers("/api/events")
                                    .hasAuthority("EVENT_READ")

                                    .anyRequest()
                                    .authenticated()
                    );

            return http.build();
        }
    }
}