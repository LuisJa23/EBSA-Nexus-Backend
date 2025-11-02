package co.com.ebsa.ebsa_nexus.application.service;

import co.com.ebsa.ebsa_nexus.application.factories.CrewMemberFactory;
import co.com.ebsa.ebsa_nexus.domain.entity.Crew;
import co.com.ebsa.ebsa_nexus.domain.entity.CrewMember;
import co.com.ebsa.ebsa_nexus.domain.enums.CrewStatus;
import co.com.ebsa.ebsa_nexus.domain.exception.crew.CannotRemoveLastMemberException;
import co.com.ebsa.ebsa_nexus.domain.exception.crew.CrewHasNoLeaderException;
import co.com.ebsa.ebsa_nexus.domain.exception.crew.InvalidCrewStatusException;
import co.com.ebsa.ebsa_nexus.domain.exception.crew.UserAlreadyInCrewException;
import co.com.ebsa.ebsa_nexus.domain.repository.CrewMemberRepository;
import co.com.ebsa.ebsa_nexus.domain.repository.CrewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CrewMemberServiceTest {

    @Mock private CrewMemberRepository memberRepository;
    @Mock private CrewRepository crewRepository;
    @Mock private CrewMemberFactory memberFactory;
    @Mock private NotificationApplicationService notificationService;

    @InjectMocks private CrewMemberService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        service = new CrewMemberService(memberRepository, crewRepository, memberFactory, notificationService);
    }

    private Crew availableCrew(Long id) {
        return Crew.builder().id(id).name("C").status(CrewStatus.DISPONIBLE).build();
    }

    @Test
    void addMember_happyPath() {
        when(crewRepository.findActiveById(1L)).thenReturn(Optional.of(availableCrew(1L)));
        when(memberRepository.isUserInActiveCrew(2L)).thenReturn(false);
        CrewMember cm = CrewMember.builder().id(5L).crewId(1L).userId(2L).build();
        when(memberFactory.createMember(1L, 2L)).thenReturn(cm);
        when(memberRepository.save(cm)).thenReturn(cm);

        CrewMember saved = service.addMember(1L, 2L);
        assertThat(saved.getId()).isEqualTo(5L);
    }

    @Test
    void addMember_userAlreadyInCrew_throws() {
        when(crewRepository.findActiveById(1L)).thenReturn(Optional.of(availableCrew(1L)));
        when(memberRepository.isUserInActiveCrew(2L)).thenReturn(true);
        assertThatThrownBy(() -> service.addMember(1L, 2L))
                .isInstanceOf(UserAlreadyInCrewException.class);
    }

    @Test
    void removeMember_lastMember_throws() {
        when(crewRepository.findActiveById(1L)).thenReturn(Optional.of(availableCrew(1L)));
        when(memberRepository.countActiveMembers(1L)).thenReturn(1);
        assertThatThrownBy(() -> service.removeMember(1L, 2L))
                .isInstanceOf(CannotRemoveLastMemberException.class);
    }

    @Test
    void removeLeader_withoutReplacement_throws() {
        when(crewRepository.findActiveById(1L)).thenReturn(Optional.of(availableCrew(1L)));
        when(memberRepository.countActiveMembers(1L)).thenReturn(2);
        CrewMember leader = CrewMember.builder().crewId(1L).userId(2L).isLeader(true).build();
        when(memberRepository.findActiveMembership(1L, 2L)).thenReturn(Optional.of(leader));
        when(memberRepository.countActiveLeaders(1L)).thenReturn(1);

        assertThatThrownBy(() -> service.removeMember(1L, 2L))
                .isInstanceOf(CrewHasNoLeaderException.class);
    }

    @Test
    void addLeader_whenAlreadyLeaderExists_throws() {
        when(crewRepository.findActiveById(1L)).thenReturn(Optional.of(availableCrew(1L)));
        when(memberRepository.countActiveLeaders(1L)).thenReturn(1);
        assertThatThrownBy(() -> service.addLeader(1L, 2L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already has a leader");
    }

    @Test
    void addMember_whenCrewNotAvailable_throws() {
        Crew busy = Crew.builder().id(1L).status(CrewStatus.EN_ATENCION).build();
        when(crewRepository.findActiveById(1L)).thenReturn(Optional.of(busy));
        assertThatThrownBy(() -> service.addMember(1L, 2L))
                .isInstanceOf(InvalidCrewStatusException.class);
    }
}
