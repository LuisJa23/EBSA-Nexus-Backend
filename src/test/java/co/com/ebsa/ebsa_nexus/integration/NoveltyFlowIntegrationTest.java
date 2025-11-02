package co.com.ebsa.ebsa_nexus.integration;

import co.com.ebsa.ebsa_nexus.application.dto.request.novelty.AssignCrewRequest;
import co.com.ebsa.ebsa_nexus.domain.entity.Novelty;
import co.com.ebsa.ebsa_nexus.domain.enums.NoveltyReason;
import co.com.ebsa.ebsa_nexus.domain.enums.NoveltyStatus;
import co.com.ebsa.ebsa_nexus.infrastructure.persistence.jpa.repositories.JpaNoveltyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NoveltyFlowIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired JpaNoveltyRepository noveltyRepository;

    private Long noveltyId;

    @BeforeEach
    void setup() {
        noveltyRepository.deleteAll();
        Novelty n = new Novelty();
        n.setAreaId(1L);
        n.setReason(NoveltyReason.ERROR_LECTURA);
        n.setAccountNumber("ACC-1");
        n.setMeterNumber("MTR-1");
        n.setStatus(NoveltyStatus.CREADA);
        n.setCreatedBy(1L);
        n.setCreatedAt(LocalDateTime.now());
        n.setUpdatedAt(LocalDateTime.now());
        noveltyId = noveltyRepository.save(n).getId();
    }

    @Test
    void assign_resolve_verify_flow() throws Exception {
        AssignCrewRequest req = new AssignCrewRequest();
        req.setAssignedCrewId(123L);
        req.setPriority("MEDIA");
        req.setInstructions("Proceed");

        mockMvc.perform(post("/api/v1/novelties/" + noveltyId + "/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        // Mark as resolved by a worker
        mockMvc.perform(put("/api/v1/novelties/" + noveltyId + "/resolve")
                        .with(user("worker").roles("TRABAJADOR"))
                        .requestAttr("userId", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"resolutionNotes\":\"fixed\"}"))
                .andExpect(status().isOk());

        // Verify (close) by admin
        mockMvc.perform(put("/api/v1/novelties/" + noveltyId + "/verify")
                        .with(user("admin").roles("ADMIN"))
                        .requestAttr("userId", 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk());

        Novelty after = noveltyRepository.findById(noveltyId).orElseThrow();
        assertThat(after.getStatus()).isIn(NoveltyStatus.CERRADA, NoveltyStatus.COMPLETADA);
    }
}
