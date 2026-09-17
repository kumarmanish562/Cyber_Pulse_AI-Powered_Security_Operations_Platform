package cyberpulse.risk.controller;

import cyberpulse.auth.security.JwtService;
import cyberpulse.risk.service.RiskAssessmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RiskAssessmentController.class)
class RiskAssessmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private RiskAssessmentService riskAssessmentService;

    @Test
    @WithMockUser(authorities = "EVENT_READ")
    void shouldReturn403WithoutRiskRead() throws Exception {

        mockMvc.perform(
                get("/api/risk-assessments")
        ).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "RISK_READ")
    void shouldAllowRiskRead() throws Exception {

        mockMvc.perform(
                get("/api/risk-assessments")
        ).andExpect(status().isOk());
    }
}