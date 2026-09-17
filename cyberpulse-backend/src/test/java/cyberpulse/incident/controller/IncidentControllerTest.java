package cyberpulse.incident.controller;

import cyberpulse.incident.dto.IncidentResponse;
import cyberpulse.incident.entity.IncidentSeverity;
import cyberpulse.incident.entity.IncidentStatus;
import cyberpulse.incident.service.IncidentService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class IncidentControllerTest {

    private MockMvc mockMvc;

    private IncidentService incidentService;


    @BeforeEach
    void setUp() {

        incidentService =
                mock(IncidentService.class);

        IncidentController controller =
                new IncidentController(
                        incidentService
                );

        PageableHandlerMethodArgumentResolver pageableResolver =
                new PageableHandlerMethodArgumentResolver();

        mockMvc =
                MockMvcBuilders
                        .standaloneSetup(controller)
                        .setCustomArgumentResolvers(
                                pageableResolver
                        )
                        .build();
    }


    // =========================================================
    // GET ALL
    // =========================================================

    @Test
    void shouldGetAllIncidents()
            throws Exception {

        Page<IncidentResponse> page =
                new PageImpl<>(
                        List.of(),
                        PageRequest.of(0, 20),
                        0
                );

        when(
                incidentService.getAll(any())
        ).thenReturn(page);


        mockMvc.perform(
                        get("/api/incidents")
                )
                .andExpect(
                        status().isOk()
                );


        verify(
                incidentService
        ).getAll(any());
    }


    // =========================================================
    // GET BY ID
    // =========================================================

    @Test
    void shouldGetIncidentById()
            throws Exception {

        UUID id =
                UUID.randomUUID();

        IncidentResponse response =
                createResponse(id);


        when(
                incidentService.getById(id)
        ).thenReturn(response);


        mockMvc.perform(
                        get(
                                "/api/incidents/{id}",
                                id
                        )
                )
                .andExpect(
                        status().isOk()
                );


        verify(
                incidentService
        ).getById(id);
    }


    // =========================================================
    // UPDATE STATUS
    // =========================================================

    @Test
    void shouldUpdateIncidentStatus()
            throws Exception {

        UUID id =
                UUID.randomUUID();


        IncidentResponse response =
                createResponse(id);


        when(
                incidentService.updateStatus(
                        eq(id),
                        eq(
                                IncidentStatus.INVESTIGATING
                        )
                )
        ).thenReturn(response);


        mockMvc.perform(
                        patch(
                                "/api/incidents/{id}/status",
                                id
                        )
                                .contentType(
                                        "application/json"
                                )
                                .content("""
                                        {
                                            "status": "INVESTIGATING"
                                        }
                                        """)
                )
                .andExpect(
                        status().isOk()
                );


        verify(
                incidentService
        ).updateStatus(
                id,
                IncidentStatus.INVESTIGATING
        );
    }


    // =========================================================
    // TEST DATA
    // =========================================================

    private IncidentResponse createResponse(
            UUID id
    ) {

        return new IncidentResponse(

                id,

                "INC-2026-000001",

                "Test Incident",

                "Test description",

                IncidentStatus.OPEN,

                IncidentSeverity.HIGH,

                UUID.randomUUID(),

                null,

                null,

                null,

                null,

                null
        );
    }
}