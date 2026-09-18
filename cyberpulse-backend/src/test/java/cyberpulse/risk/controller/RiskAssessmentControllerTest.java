package cyberpulse.risk.controller;

import cyberpulse.auth.security.JwtService;
import cyberpulse.risk.service.RiskAssessmentService;

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
class RiskAssessmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RiskAssessmentService riskAssessmentService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;


    @Test
    void shouldAllowRiskRead() throws Exception {

        mockMvc.perform(
                get("/api/v1/risk-assessments")
                        .with(
                                user("analyst")
                                        .authorities(
                                                new SimpleGrantedAuthority("ROLE_ANALYST"),
                                                new SimpleGrantedAuthority("RISK_READ")
                                        )
                        )
        ).andExpect(status().isOk());
    }


    @Test
    void shouldReturn403WithoutRiskRead() throws Exception {

        mockMvc.perform(
                get("/api/v1/risk-assessments")
                        .with(
                                user("analyst")
                                        .authorities(
                                                new SimpleGrantedAuthority("ROLE_ANALYST")
                                        )
                        )
        ).andExpect(status().isForbidden());
    }
}