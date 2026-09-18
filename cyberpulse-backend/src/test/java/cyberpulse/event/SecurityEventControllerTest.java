package cyberpulse.event;

import cyberpulse.auth.security.JwtService;
import cyberpulse.event.service.SecurityEventService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;


    @Test
    void unauthenticatedUserShouldReceive401() throws Exception {

        mockMvc.perform(
                        get("/api/v1/events")
                                .with(anonymous())
                )
                .andExpect(status().isUnauthorized());
    }


    @Test
    void viewerWithEventReadShouldReceive200() throws Exception {

        mockMvc.perform(
                        get("/api/v1/events")
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
                        get("/api/v1/events")
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