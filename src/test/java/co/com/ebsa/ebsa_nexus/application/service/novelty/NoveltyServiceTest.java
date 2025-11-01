package co.com.ebsa.ebsa_nexus.application.service.novelty;

import co.com.ebsa.ebsa_nexus.application.dto.request.novelty.AssignCrewRequest;
import co.com.ebsa.ebsa_nexus.application.dto.response.NoveltyResponse;
import co.com.ebsa.ebsa_nexus.domain.entity.Novelty;
import co.com.ebsa.ebsa_nexus.domain.entity.NoveltyAssignment;
import co.com.ebsa.ebsa_nexus.domain.enums.NoveltyStatus;
import co.com.ebsa.ebsa_nexus.domain.exception.novelty.NoveltyOperationException;
import co.com.ebsa.ebsa_nexus.domain.repository.*;
import co.com.ebsa.ebsa_nexus.infrastructure.storage.FirebaseStorageAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class NoveltyServiceTest {

    @Mock private NoveltyRepository noveltyRepository;
    @Mock private NoveltyImageRepository noveltyImageRepository;
    @Mock private NoveltyAssignmentRepository noveltyAssignmentRepository;
    @Mock private NoveltyValidationService validationService;
    @Mock private NoveltyNotificationService notificationService;
    @Mock private FirebaseStorageAdapter firebaseStorageAdapter;
    @Mock private CrewMemberRepository crewMemberRepository;

    @InjectMocks private NoveltyService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        service = new NoveltyService(noveltyRepository, noveltyImageRepository, noveltyAssignmentRepository,
                validationService, notificationService, firebaseStorageAdapter, crewMemberRepository);
    }

    private Novelty newNovelty(Long id, NoveltyStatus status) {
        Novelty n = new Novelty();
        n.setId(id);
        n.setStatus(status);
        n.setObservations("");
        return n;
    }

    @Test
    void assignCrew_happyPath_updatesStatus_andPersistsAssignment() {
        Long noveltyId = 1L;
        Novelty n = newNovelty(noveltyId, NoveltyStatus.CREADA);
        when(noveltyRepository.findById(noveltyId)).thenReturn(Optional.of(n));

        AssignCrewRequest req = new AssignCrewRequest();
        req.setAssignedCrewId(100L);
        req.setInstructions("Do it");
        req.setPriority("ALTA");
        req.setEstimatedResolutionDate(LocalDate.now().plusDays(1));

        when(noveltyImageRepository.findByNoveltyIdOrderByUploadedAtDesc(noveltyId))
                .thenReturn(Collections.emptyList());

        NoveltyResponse resp = service.assignCrew(noveltyId, req, 5L);

        assertThat(resp.getCrewId()).isEqualTo(100L);
        verify(validationService).validateAdminCanAssign(5L);
        verify(noveltyAssignmentRepository).save(any(NoveltyAssignment.class));
        verify(notificationService).notifyCrewAssignment(any(Novelty.class), any(NoveltyAssignment.class));
        verify(noveltyRepository, atLeastOnce()).save(any(Novelty.class));
    }

    @Test
    void resolveNovelty_requiresEnCursoStatus() {
        Long noveltyId = 2L;
        Novelty n = newNovelty(noveltyId, NoveltyStatus.CREADA);
        when(noveltyRepository.findById(noveltyId)).thenReturn(Optional.of(n));

        assertThatThrownBy(() -> service.resolveNovelty(noveltyId, "done", 7L))
                .isInstanceOf(NoveltyOperationException.class)
                .hasMessageContaining("EN_CURSO");
    }

    @Test
    void resolveNovelty_happyPath_marksCompleted_andNotifies() {
        Long noveltyId = 3L;
        Novelty n = newNovelty(noveltyId, NoveltyStatus.EN_CURSO);
        when(noveltyRepository.findById(noveltyId)).thenReturn(Optional.of(n));
        when(noveltyImageRepository.findByNoveltyIdOrderByUploadedAtDesc(noveltyId))
                .thenReturn(Collections.emptyList());
        when(noveltyAssignmentRepository.findLatestByNoveltyId(noveltyId))
                .thenReturn(Optional.empty());

        NoveltyResponse resp = service.resolveNovelty(noveltyId, "ok", 7L);

        assertThat(resp.getStatus()).isEqualTo(NoveltyStatus.COMPLETADA);
        verify(validationService).validateCanResolve(7L, n);
        verify(notificationService).notifyResolution(n);
    }

    @Test
    void verifyResolution_approved_closesNovelty() {
        Long noveltyId = 4L;
        Novelty n = newNovelty(noveltyId, NoveltyStatus.COMPLETADA);
        when(noveltyRepository.findById(noveltyId)).thenReturn(Optional.of(n));
        when(noveltyImageRepository.findByNoveltyIdOrderByUploadedAtDesc(noveltyId))
                .thenReturn(Collections.emptyList());
        when(noveltyAssignmentRepository.findLatestByNoveltyId(noveltyId))
                .thenReturn(Optional.empty());

        NoveltyResponse resp = service.verifyResolution(noveltyId, true, "ok", 1L);
        assertThat(resp.getStatus()).isEqualTo(NoveltyStatus.CERRADA);
        verify(validationService).validateAdminCanVerify(1L);
    }

    @Test
    void verifyResolution_rejected_returnsToEnCurso_andNotifiesRejection() {
        Long noveltyId = 5L;
        Novelty n = newNovelty(noveltyId, NoveltyStatus.COMPLETADA);
        when(noveltyRepository.findById(noveltyId)).thenReturn(Optional.of(n));
        when(noveltyImageRepository.findByNoveltyIdOrderByUploadedAtDesc(noveltyId))
                .thenReturn(Collections.emptyList());
        when(noveltyAssignmentRepository.findLatestByNoveltyId(noveltyId))
                .thenReturn(Optional.empty());

        NoveltyResponse resp = service.verifyResolution(noveltyId, false, "revise", 1L);
        assertThat(resp.getStatus()).isEqualTo(NoveltyStatus.EN_CURSO);
        verify(notificationService).notifyRejection(n);
    }
}
