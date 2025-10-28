package co.com.ebsa.ebsa_nexus.integration;

import co.com.ebsa.ebsa_nexus.application.dto.request.crew.AddNotesRequest;
import co.com.ebsa.ebsa_nexus.application.dto.request.crew.AssignIncidentRequest;
import co.com.ebsa.ebsa_nexus.application.dto.request.crew.CancelAssignmentRequest;
import co.com.ebsa.ebsa_nexus.application.dto.request.crew.CompleteAssignmentRequest;
import co.com.ebsa.ebsa_nexus.application.dto.response.ApiResponse;
import co.com.ebsa.ebsa_nexus.application.dto.response.IncidentAssignmentResponse;
import co.com.ebsa.ebsa_nexus.application.service.IncidentAssignmentService;
import co.com.ebsa.ebsa_nexus.domain.entity.IncidentAssignment;
import co.com.ebsa.ebsa_nexus.domain.enums.AssignmentStatus;
import co.com.ebsa.ebsa_nexus.domain.exception.crew.InvalidAssignmentStatusException;
import co.com.ebsa.ebsa_nexus.presentation.controller.IncidentAssignmentController;
import co.com.ebsa.ebsa_nexus.presentation.mapper.IncidentAssignmentDtoMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = IncidentAssignmentController.class)
class IncidentAssignmentControllerIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean IncidentAssignmentService assignmentService;
    @MockBean IncidentAssignmentDtoMapper mapper;

    private IncidentAssignment assignment(Long id, Long crewId, Long incidentId, AssignmentStatus status) {
        IncidentAssignment ia = new IncidentAssignment();
        ia.setId(id);
        ia.setCrewId(crewId);
        ia.setIncidentId(incidentId);
        ia.setStatus(status);
        return ia;
    }

    private IncidentAssignmentResponse responseOf(IncidentAssignment ia) {
        IncidentAssignmentResponse r = new IncidentAssignmentResponse();
        r.setId(ia.getId());
        r.setCrewId(ia.getCrewId());
        r.setIncidentId(ia.getIncidentId());
        r.setStatus(ia.getStatus());
        return r;
    }

    @Test
    void assignIncident_created201_and_mapsResponse() throws Exception {
        AssignIncidentRequest req = AssignIncidentRequest.builder()
                .incidentId(10L).crewId(1L).assignedBy(99L).notes("initial")
                .build();
        IncidentAssignment ia = assignment(5L, 1L, 10L, AssignmentStatus.ASIGNADO);
        IncidentAssignmentResponse resp = responseOf(ia);
        when(assignmentService.assignIncidentWithNotes(1L, 10L, 99L, "initial")).thenReturn(ia);
        when(mapper.toResponse(ia)).thenReturn(resp);

        mockMvc.perform(post("/api/assignments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(5L))
                .andExpect(jsonPath("$.data.status").value("ASIGNADO"));
    }

    @Test
    void start_then_complete_assignment_ok() throws Exception {
        IncidentAssignment started = assignment(7L, 2L, 20L, AssignmentStatus.EN_CURSO);
        IncidentAssignmentResponse startedResp = responseOf(started);
        when(assignmentService.startAssignment(7L)).thenReturn(started);
        when(mapper.toResponse(started)).thenReturn(startedResp);

        mockMvc.perform(patch("/api/assignments/7/start"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("EN_CURSO"));

        IncidentAssignment completed = assignment(7L, 2L, 20L, AssignmentStatus.COMPLETADO);
        IncidentAssignmentResponse completedResp = responseOf(completed);
        when(assignmentService.completeAssignment(7L)).thenReturn(completed);
        when(mapper.toResponse(completed)).thenReturn(completedResp);

        mockMvc.perform(patch("/api/assignments/7/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CompleteAssignmentRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("COMPLETADO"));
    }

    @Test
    void cancel_assignment_ok() throws Exception {
        IncidentAssignment cancelled = assignment(9L, 3L, 30L, AssignmentStatus.CANCELADO);
        IncidentAssignmentResponse cancelledResp = responseOf(cancelled);
        when(assignmentService.cancelAssignment(eq(9L), anyString())).thenReturn(cancelled);
        when(mapper.toResponse(cancelled)).thenReturn(cancelledResp);

        CancelAssignmentRequest body = new CancelAssignmentRequest();
        body.setReason("duplicate");

        mockMvc.perform(patch("/api/assignments/9/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CANCELADO"));
    }

    @Test
    void add_notes_ok() throws Exception {
        IncidentAssignment ia = assignment(11L, 5L, 50L, AssignmentStatus.ASIGNADO);
        IncidentAssignmentResponse resp = responseOf(ia);
        when(assignmentService.addNotes(eq(11L), anyString())).thenReturn(ia);
        when(mapper.toResponse(ia)).thenReturn(resp);

        AddNotesRequest body = new AddNotesRequest();
        body.setNotes("note");

        mockMvc.perform(post("/api/assignments/11/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(11L));
    }

    @Test
    void queries_endpoints_ok() throws Exception {
        IncidentAssignment ia = assignment(13L, 6L, 60L, AssignmentStatus.ASIGNADO);
        IncidentAssignmentResponse resp = responseOf(ia);
        when(assignmentService.getAssignmentById(13L)).thenReturn(ia);
        when(mapper.toResponse(ia)).thenReturn(resp);

        mockMvc.perform(get("/api/assignments/13"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(13L));

        when(assignmentService.getAssignmentsByCrew(6L)).thenReturn(List.of(ia));
        mockMvc.perform(get("/api/assignments/crew/6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].crewId").value(6L));

        when(assignmentService.getAssignmentsByIncident(60L)).thenReturn(List.of(ia));
        mockMvc.perform(get("/api/assignments/incident/60"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].incidentId").value(60L));

        when(assignmentService.getAssignmentsByStatus(AssignmentStatus.ASIGNADO)).thenReturn(List.of(ia));
        mockMvc.perform(get("/api/assignments/status/ASIGNADO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].status").value("ASIGNADO"));

        when(assignmentService.getAssignmentsByDateRange(any(), any())).thenReturn(List.of(ia));
        mockMvc.perform(get("/api/assignments/date-range")
                        .param("startDate", LocalDateTime.now().minusDays(1).toString())
                        .param("endDate", LocalDateTime.now().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(13L));

        when(assignmentService.hasOpenAssignments(6L)).thenReturn(true);
        mockMvc.perform(get("/api/assignments/crew/6/has-open"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true));

        when(assignmentService.countActiveAssignments(6L)).thenReturn(1L);
        mockMvc.perform(get("/api/assignments/crew/6/count/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(1));

        when(assignmentService.countCompletedAssignments(6L)).thenReturn(2L);
        mockMvc.perform(get("/api/assignments/crew/6/count/completed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(2));
    }

    @Test
    void regression_invalid_transition_returns_4xx() throws Exception {
        when(assignmentService.startAssignment(100L))
                .thenThrow(new InvalidAssignmentStatusException(AssignmentStatus.EN_CURSO, "start assignment"));

        mockMvc.perform(patch("/api/assignments/100/start"))
                .andExpect(status().is4xxClientError());
    }
}
