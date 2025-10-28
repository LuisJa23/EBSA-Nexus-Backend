package co.com.ebsa.ebsa_nexus.application.service;

import co.com.ebsa.ebsa_nexus.application.factories.IncidentAssignmentFactory;
import co.com.ebsa.ebsa_nexus.domain.entity.Crew;
import co.com.ebsa.ebsa_nexus.domain.entity.CrewMember;
import co.com.ebsa.ebsa_nexus.domain.entity.IncidentAssignment;
import co.com.ebsa.ebsa_nexus.domain.enums.AssignmentStatus;
import co.com.ebsa.ebsa_nexus.domain.enums.CrewStatus;
import co.com.ebsa.ebsa_nexus.domain.exception.crew.CrewNotFoundException;
import co.com.ebsa.ebsa_nexus.domain.exception.crew.InvalidAssignmentStatusException;
import co.com.ebsa.ebsa_nexus.domain.exception.crew.InvalidCrewStatusException;
import co.com.ebsa.ebsa_nexus.domain.repository.CrewRepository;
import co.com.ebsa.ebsa_nexus.domain.repository.IncidentAssignmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class IncidentAssignmentServiceTest {

    @Mock private IncidentAssignmentRepository assignmentRepository;
    @Mock private CrewRepository crewRepository;
    @Mock private IncidentAssignmentFactory assignmentFactory;
    @Mock private CrewService crewService;
    @Mock private CrewMemberService crewMemberService;
    @Mock private NotificationApplicationService notificationService;

    @InjectMocks private IncidentAssignmentService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        service = new IncidentAssignmentService(assignmentRepository, crewRepository, assignmentFactory, crewService, crewMemberService, notificationService);
    }

    private Crew activeCrew(Long id, CrewStatus status) {
        Crew c = Crew.builder().id(id).name("C1").status(status).build();
        return c;
    }

    @Test
    void assignIncident_happyPath_marksNotifications() {
        Long crewId = 1L, incidentId = 10L, assignedBy = 99L;
        Crew crew = activeCrew(crewId, CrewStatus.DISPONIBLE);

        when(crewRepository.findActiveById(crewId)).thenReturn(Optional.of(crew));
        when(assignmentRepository.findActiveAssignment(incidentId)).thenReturn(Optional.empty());
        IncidentAssignment assignment = new IncidentAssignment();
        when(assignmentFactory.createAssignment(crewId, incidentId, assignedBy)).thenReturn(assignment);
        when(assignmentRepository.save(assignment)).thenReturn(assignment);
        when(crewMemberService.getActiveMembers(crewId)).thenReturn(List.of(
                CrewMember.builder().userId(1L).isLeader(true).build(),
                CrewMember.builder().userId(2L).isLeader(false).build()
        ));

        IncidentAssignment saved = service.assignIncident(crewId, incidentId, assignedBy);

        assertThat(saved).isNotNull();
        verify(notificationService, atLeastOnce()).createNotification(anyLong(), anyString(), anyString(), anyString(), isNull());
    }

    @Test
    void assignIncident_crewNotFound_throws() {
        when(crewRepository.findActiveById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.assignIncident(1L, 10L, 99L))
                .isInstanceOf(CrewNotFoundException.class);
    }

    @Test
    void assignIncident_crewNotAvailable_throws() {
        when(crewRepository.findActiveById(1L)).thenReturn(Optional.of(activeCrew(1L, CrewStatus.EN_ATENCION)));
        assertThatThrownBy(() -> service.assignIncident(1L, 10L, 99L))
                .isInstanceOf(InvalidCrewStatusException.class);
    }

    @Test
    void startAssignment_transitions_and_marksCrewInAttention() {
        IncidentAssignment ia = new IncidentAssignment();
        ia.setId(5L);
        ia.setCrewId(1L);
        ia.setStatus(AssignmentStatus.ASIGNADO);
        when(assignmentRepository.findById(5L)).thenReturn(Optional.of(ia));
        when(assignmentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        IncidentAssignment updated = service.startAssignment(5L);
        assertThat(updated.getStatus()).isEqualTo(AssignmentStatus.EN_CURSO);
        verify(crewService).markAsInAttention(1L);
    }

    @Test
    void completeAssignment_finishes_and_marksCrewAvailable_whenNoOpenAssignments() {
        IncidentAssignment ia = new IncidentAssignment();
        ia.setId(5L); ia.setCrewId(1L); ia.setStatus(AssignmentStatus.EN_CURSO);
        when(assignmentRepository.findById(5L)).thenReturn(Optional.of(ia));
        when(assignmentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(assignmentRepository.hasOpenAssignments(1L)).thenReturn(false);

        IncidentAssignment updated = service.completeAssignment(5L);
        assertThat(updated.getStatus()).isEqualTo(AssignmentStatus.COMPLETADO);
        verify(crewService).markAsAvailable(1L);
    }

    @Test
    void startAssignment_wrongStatus_throws() {
        IncidentAssignment ia = new IncidentAssignment();
        ia.setStatus(AssignmentStatus.EN_CURSO);
        when(assignmentRepository.findById(5L)).thenReturn(Optional.of(ia));
        assertThatThrownBy(() -> service.startAssignment(5L))
                .isInstanceOf(InvalidAssignmentStatusException.class);
    }
}
