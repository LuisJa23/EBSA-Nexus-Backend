package co.com.ebsa.ebsa_nexus.application.service.novelty;

import co.com.ebsa.ebsa_nexus.domain.entity.Novelty;
import co.com.ebsa.ebsa_nexus.domain.entity.NoveltyAssignment;
import co.com.ebsa.ebsa_nexus.domain.enums.NoveltyStatus;
import co.com.ebsa.ebsa_nexus.domain.exception.novelty.NoveltyOperationException;
import co.com.ebsa.ebsa_nexus.domain.repository.NoveltyAssignmentRepository;
import org.springframework.stereotype.Service;

/**
 * Service to validate business rules for novelty operations
 */
@Service
public class NoveltyValidationService {

    private final NoveltyAssignmentRepository noveltyAssignmentRepository;
    // TODO: Inject UserRepository when available to validate user roles
    // private final UserRepository userRepository;

    public NoveltyValidationService(NoveltyAssignmentRepository noveltyAssignmentRepository) {
        this.noveltyAssignmentRepository = noveltyAssignmentRepository;
    }

    /**
     * Validate that user is a supervisor and can report novelties
     */
    public void validateSupervisorCanReport(Long userId) {
        // TODO: Implement role check when User entity is available
        // User user = userRepository.findById(userId)
        //     .orElseThrow(() -> new NoveltyOperationException("User not found"));
        // if (!user.getRole().equals("SUPERVISOR")) {
        //     throw new NoveltyOperationException("Only supervisors can report novelties");
        // }
        
        if (userId == null) {
            throw new NoveltyOperationException("User ID is required");
        }
    }

    /**
     * Validate that crew exists
     */
    public void validateCrewExists(Long crewId) {
        // TODO: Implement crew validation when Crew entity is available
        // Crew crew = crewRepository.findById(crewId)
        //     .orElseThrow(() -> new NoveltyOperationException("Crew not found with id: " + crewId));
        
        if (crewId == null) {
            throw new NoveltyOperationException("Crew ID is required");
        }
    }

    /**
     * Validate that user can upload images for the novelty
     */
    public void validateCanUploadImages(Long userId, Novelty novelty) {
        // Users who can upload images:
        // 1. The user who created the novelty
        // 2. The crew assigned to resolve the novelty
        // 3. Admin users
        
        // TODO: Implement full validation when User entity is available
        // For now, basic validation
        if (userId == null) {
            throw new NoveltyOperationException("User ID is required");
        }

        // Check if novelty is in a state that allows image uploads
        if (novelty.getStatus() == NoveltyStatus.CERRADA || novelty.getStatus() == NoveltyStatus.CANCELADA) {
            throw new NoveltyOperationException("Cannot upload images to a CERRADA or CANCELADA novelty");
        }
    }

    /**
     * Validate that user is an admin and can assign crews
     */
    public void validateAdminCanAssign(Long userId) {
        // TODO: Implement role check when User entity is available
        // User user = userRepository.findById(userId)
        //     .orElseThrow(() -> new NoveltyOperationException("User not found"));
        // if (!user.getRole().equals("ADMIN")) {
        //     throw new NoveltyOperationException("Only admins can assign crews");
        // }
        
        // Comentado temporalmente para pruebas sin autenticación
        // if (userId == null) {
        //     throw new NoveltyOperationException("User ID is required");
        // }
    }

    /**
     * Validate that novelty status allows assignment
     */
    public void validateStatusForAssignment(Novelty novelty) {
        // Can only assign if status is CREADA (new novelty)
        if (novelty.getStatus() != NoveltyStatus.CREADA) {
            throw new NoveltyOperationException(
                "Novelty must be in CREADA status to be assigned. Current status: " + novelty.getStatus()
            );
        }
    }

    /**
     * Validate that user is part of the assigned crew
     */
    public void validateUserIsAssignedCrew(Long userId, Novelty novelty) {
        if (userId == null) {
            throw new NoveltyOperationException("User ID is required");
        }
        
        // Check if novelty is assigned
        NoveltyAssignment assignment = noveltyAssignmentRepository.findByNoveltyIdOrderByAssignedAtDesc(novelty.getId())
                .stream()
                .findFirst()
                .orElseThrow(() -> new NoveltyOperationException("Novelty is not assigned to any crew"));

        // TODO: Validate user is member of assignment.getAssignedCrewId()
        // When User-Crew relationship is available, uncomment:
        // CrewMember member = crewMemberRepository.findByCrewIdAndUserId(assignment.getAssignedCrewId(), userId)
        //     .orElseThrow(() -> new NoveltyOperationException("User is not part of the assigned crew"));
        
        // For now, we just verify that the novelty has an assignment
        if (assignment.getAssignedCrewId() == null) {
            throw new NoveltyOperationException("Assignment does not have a valid crew");
        }
    }

    /**
     * Validate that user can resolve the novelty
     */
    public void validateCanResolve(Long userId, Novelty novelty) {
        // Can resolve if:
        // 1. User is part of the assigned crew
        // 2. User is an admin
        
        // TODO: Implement full validation when User entity is available
        validateUserIsAssignedCrew(userId, novelty);
    }

    /**
     * Validate that user is an admin and can verify resolutions
     */
    public void validateAdminCanVerify(Long userId) {
        // TODO: Implement role check when User entity is available
        // User user = userRepository.findById(userId)
        //     .orElseThrow(() -> new NoveltyOperationException("User not found"));
        // if (!user.getRole().equals("ADMIN")) {
        //     throw new NoveltyOperationException("Only admins can verify novelty resolutions");
        // }
        
        if (userId == null) {
            throw new NoveltyOperationException("User ID is required");
        }
    }

    /**
     * Validate that user is an admin and can cancel novelties
     */
    public void validateAdminCanCancel(Long userId) {
        // TODO: Implement role check when User entity is available
        // User user = userRepository.findById(userId)
        //     .orElseThrow(() -> new NoveltyOperationException("User not found"));
        // if (!user.getRole().equals("ADMIN")) {
        //     throw new NoveltyOperationException("Only admins can cancel novelties");
        // }
        
        if (userId == null) {
            throw new NoveltyOperationException("User ID is required");
        }
    }

    /**
     * Validate that user can view the novelty
     */
    public void validateCanView(Long userId, Novelty novelty) {
        // All authenticated users can view novelties
        // TODO: Implement more granular permissions if needed
        // For example: supervisors only see their crew's novelties
        
        if (userId == null) {
            throw new NoveltyOperationException("User ID is required");
        }
    }
}
