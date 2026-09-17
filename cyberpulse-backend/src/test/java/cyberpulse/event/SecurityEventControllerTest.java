package cyberpulse.event;

import cyberpulse.auth.security.JwtAuthenticationFilter;
import cyberpulse.auth.security.JwtService;
import cyberpulse.common.exception.RestAccessDeniedHandler;
import cyberpulse.common.exception.RestAuthenticationEntryPoint;
import cyberpulse.event.service.SecurityEventService;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class SecurityEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SecurityEventService eventService;

    /*
     * Mock the JWT filter because these tests use
     * Spring Security Test's .with(user(...))
     * instead of testing real JWT authentication.
     */
    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private RestAuthenticationEntryPoint authenticationEntryPoint;

    @MockitoBean
    private RestAccessDeniedHandler accessDeniedHandler;


    @Test
    void unauthenticatedUserShouldReceive401() throws Exception {

        mockMvc.perform(
                        get("/api/events")
                                .with(anonymous())
                )
                .andExpect(status().isUnauthorized());
    }


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
                .andExpect(status().isOk());
    }


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
                .andExpect(status().isForbidden());
    }
}