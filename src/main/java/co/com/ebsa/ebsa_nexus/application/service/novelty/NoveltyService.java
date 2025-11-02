package co.com.ebsa.ebsa_nexus.application.service.novelty;

import co.com.ebsa.ebsa_nexus.application.dto.request.novelty.AssignCrewRequest;
import co.com.ebsa.ebsa_nexus.application.dto.request.novelty.CreateNoveltyRequest;
import co.com.ebsa.ebsa_nexus.application.dto.request.novelty.ImageUploadResultDTO;
import co.com.ebsa.ebsa_nexus.application.dto.request.novelty.NoveltySearchRequest;
import co.com.ebsa.ebsa_nexus.application.dto.request.novelty.UploadImagesRequest;
import co.com.ebsa.ebsa_nexus.application.dto.response.NoveltyDetailResponse;
import co.com.ebsa.ebsa_nexus.application.dto.response.NoveltyPageResponse;
import co.com.ebsa.ebsa_nexus.application.dto.response.NoveltyResponse;
import co.com.ebsa.ebsa_nexus.domain.entity.*;
import co.com.ebsa.ebsa_nexus.domain.enums.NoveltyStatus;
import co.com.ebsa.ebsa_nexus.domain.exception.novelty.NoveltyOperationException;
import co.com.ebsa.ebsa_nexus.domain.repository.*;
import co.com.ebsa.ebsa_nexus.infrastructure.storage.FirebaseStorageAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class NoveltyService {

    private final NoveltyRepository noveltyRepository;
    private final NoveltyImageRepository noveltyImageRepository;
    private final NoveltyAssignmentRepository noveltyAssignmentRepository;
    private final NoveltyValidationService validationService;
    private final NoveltyNotificationService notificationService;
    private final FirebaseStorageAdapter firebaseStorageAdapter;
    private final CrewMemberRepository crewMemberRepository;

    public NoveltyService(
            NoveltyRepository noveltyRepository,
            NoveltyImageRepository noveltyImageRepository,
            NoveltyAssignmentRepository noveltyAssignmentRepository,
            NoveltyValidationService validationService,
            NoveltyNotificationService notificationService,
            FirebaseStorageAdapter firebaseStorageAdapter,
            CrewMemberRepository crewMemberRepository) {
        this.noveltyRepository = noveltyRepository;
        this.noveltyImageRepository = noveltyImageRepository;
        this.noveltyAssignmentRepository = noveltyAssignmentRepository;
        this.validationService = validationService;
        this.notificationService = notificationService;
        this.firebaseStorageAdapter = firebaseStorageAdapter;
        this.crewMemberRepository = crewMemberRepository;
    }

    /**
     * Create a new novelty from meter reading form
     * The novelty is created in CREADA status without crew assignment
     * Any authenticated user can create novelties
     */
    public NoveltyResponse createNovelty(CreateNoveltyRequest request, Long createdByUserId) {
        // Any authenticated user can create novelties - no role validation needed
        log.info("Creating novelty for user {}", createdByUserId);

        // Create novelty entity in CREADA status (no crew assigned yet)
        Novelty novelty = new Novelty();
        novelty.setAreaId(request.getAreaId());
        novelty.setReason(request.getReason());
        novelty.setAccountNumber(request.getAccountNumber());
        novelty.setMeterNumber(request.getMeterNumber());
        novelty.setActiveReading(request.getActiveReading());
        novelty.setReactiveReading(request.getReactiveReading());
        novelty.setMunicipality(request.getMunicipality());
        novelty.setAddress(request.getAddress());
        novelty.setDescription(request.getDescription());
        novelty.setObservations(request.getObservations());
        novelty.setCreatedBy(createdByUserId);
        novelty.setStatus(NoveltyStatus.CREADA);
        novelty.setCreatedAt(LocalDateTime.now());
        novelty.setUpdatedAt(LocalDateTime.now());

        // Upload images to Firebase first (if provided)
        List<ImageUploadResultDTO> uploadResults = new ArrayList<>();
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            log.info("Uploading {} images to Firebase for novelty", request.getImages().size());
            String folder = "novelties/" + LocalDateTime.now().getYear();
            uploadResults = firebaseStorageAdapter.uploadImages(request.getImages(), folder);
            
            // Check for upload failures
            long failedUploads = uploadResults.stream()
                .filter(r -> !r.isSuccess())
                .count();
            
            if (failedUploads > 0) {
                log.warn("{} images failed to upload", failedUploads);
            }
        }

        // Save novelty
        Novelty savedNovelty = noveltyRepository.save(novelty);

        // Save image references with Firebase URLs
        List<NoveltyImage> images = new ArrayList<>();
        for (ImageUploadResultDTO result : uploadResults) {
            if (result.isSuccess()) {
                NoveltyImage image = new NoveltyImage();
                image.setNoveltyId(savedNovelty.getId());
                image.setImageUrl(result.getPublicUrl());
                image.setUploadedByUserId(createdByUserId);
                image.setUploadedAt(LocalDateTime.now());
                images.add(noveltyImageRepository.save(image));
            }
        }
        
        log.info("Saved {} images for novelty {}", images.size(), savedNovelty.getId());

        // Send notification to admin
        notificationService.notifyNewNovelty(savedNovelty);

        return mapToResponse(savedNovelty, images, null);
    }

    /**
     * Upload images for a novelty
     */
    public NoveltyResponse uploadImages(Long noveltyId, UploadImagesRequest request, Long uploadedByUserId) {
        // Find novelty
        Novelty novelty = noveltyRepository.findById(noveltyId)
                .orElseThrow(() -> new NoveltyOperationException("Novelty not found with id: " + noveltyId));

        // Validate user can upload images
        validationService.validateCanUploadImages(uploadedByUserId, novelty);

        // Validate maximum images
        long existingImagesCount = noveltyImageRepository.countByNoveltyId(noveltyId);
        if (existingImagesCount + request.getImageUrls().size() > 5) {
            throw new NoveltyOperationException("Cannot upload more than 5 images per novelty");
        }

        // Save images
        List<NoveltyImage> images = new ArrayList<>();
        for (String imageUrl : request.getImageUrls()) {
            NoveltyImage image = new NoveltyImage();
            image.setNoveltyId(noveltyId);
            image.setImageUrl(imageUrl);
            image.setUploadedByUserId(uploadedByUserId);
            image.setUploadedAt(LocalDateTime.now());
            images.add(image);
        }
        noveltyImageRepository.saveAll(images);

        // Update novelty timestamp
        novelty.setUpdatedAt(LocalDateTime.now());
        noveltyRepository.save(novelty);

        // Fetch images for response
        List<NoveltyImage> allImages = noveltyImageRepository.findByNoveltyIdOrderByUploadedAtDesc(noveltyId);
        NoveltyAssignment assignment = noveltyAssignmentRepository.findLatestByNoveltyId(noveltyId).orElse(null);
        
        return mapToResponse(novelty, allImages, assignment);
    }

    /**
     * Assign crew to resolve novelty (Admin only)
     */
    public NoveltyResponse assignCrew(Long noveltyId, AssignCrewRequest request, Long assignedByUserId) {
        // Find novelty
        Novelty novelty = noveltyRepository.findById(noveltyId)
                .orElseThrow(() -> new NoveltyOperationException("Novelty not found with id: " + noveltyId));

        // Validate admin can assign
        validationService.validateAdminCanAssign(assignedByUserId);

        // Validate status allows assignment
        validationService.validateStatusForAssignment(novelty);

        // Validate crew exists
        validationService.validateCrewExists(request.getAssignedCrewId());

        // Create assignment
        NoveltyAssignment assignment = new NoveltyAssignment();
        assignment.setNoveltyId(noveltyId);
        assignment.setAssignedCrewId(request.getAssignedCrewId());
        assignment.setCrewId(request.getAssignedCrewId()); // Campo duplicado requerido por la DB
        // TEMPORAL: Usar ID 1 (admin) si no hay userId para pruebas sin autenticación
        Long effectiveUserId = (assignedByUserId != null) ? assignedByUserId : 1L;
        assignment.setAssignedByUserId(effectiveUserId);
        assignment.setAssignedBy(effectiveUserId); // Campo adicional requerido por la DB
        assignment.setInstructions(request.getInstructions());
        assignment.setPriority(request.getPriority());
        assignment.setEstimatedResolutionDate(request.getEstimatedResolutionDate());
        assignment.setAssignedAt(LocalDateTime.now());
        assignment.setStatus("ACTIVE"); // Estado inicial
        noveltyAssignmentRepository.save(assignment);

        // Update novelty status using entity method
        novelty.assignCrew(request.getAssignedCrewId());
        novelty.setUpdatedAt(LocalDateTime.now());
        noveltyRepository.save(novelty);

        // Send notification to assigned crew
        log.info("Llamando a notificationService.notifyCrewAssignment para novedad {} y cuadrilla {}", 
                noveltyId, request.getAssignedCrewId());
        notificationService.notifyCrewAssignment(novelty, assignment);
        log.info("Finalizada llamada a notificationService.notifyCrewAssignment");

        // Fetch images and assignment for response
        List<NoveltyImage> images = noveltyImageRepository.findByNoveltyIdOrderByUploadedAtDesc(noveltyId);
        
        return mapToResponse(novelty, images, assignment);
    }

    /**
     * Mark novelty as in progress (no longer used - crew assignment transitions to EN_CURSO)
     * Keeping for backward compatibility
     */
    public NoveltyResponse startProgress(Long noveltyId, Long userId) {
        // Find novelty
        Novelty novelty = noveltyRepository.findById(noveltyId)
                .orElseThrow(() -> new NoveltyOperationException("Novelty not found with id: " + noveltyId));

        // Validate user is assigned crew
        validationService.validateUserIsAssignedCrew(userId, novelty);

        // Validate status transition - should be EN_CURSO after crew assignment
        if (novelty.getStatus() != NoveltyStatus.EN_CURSO) {
            throw new NoveltyOperationException("Novelty is already in progress or completed");
        }

        novelty.setUpdatedAt(LocalDateTime.now());
        noveltyRepository.save(novelty);

        // Notify supervisor
        notificationService.notifyStatusChange(novelty);

        // Fetch images and assignment for response
        List<NoveltyImage> images = noveltyImageRepository.findByNoveltyIdOrderByUploadedAtDesc(noveltyId);
        NoveltyAssignment assignment = noveltyAssignmentRepository.findLatestByNoveltyId(noveltyId).orElse(null);
        
        return mapToResponse(novelty, images, assignment);
    }

    /**
     * Mark novelty as completed with completion details
     */
    public NoveltyResponse resolveNovelty(Long noveltyId, String completionNotes, Long completedByUserId) {
        // Find novelty
        Novelty novelty = noveltyRepository.findById(noveltyId)
                .orElseThrow(() -> new NoveltyOperationException("Novelty not found with id: " + noveltyId));

        // Validate user can complete
        validationService.validateCanResolve(completedByUserId, novelty);

        // Validate status allows completion
        if (novelty.getStatus() != NoveltyStatus.EN_CURSO) {
            throw new NoveltyOperationException("Novelty must be in EN_CURSO status to be completed");
        }

        // Update novelty using entity method
        novelty.markAsCompleted();
        if (completionNotes != null) {
            novelty.setObservations(novelty.getObservations() + "\n\nNotas de completación: " + completionNotes);
        }
        novelty.setUpdatedAt(LocalDateTime.now());
        noveltyRepository.save(novelty);

        // Notify admin and supervisor
        notificationService.notifyResolution(novelty);

        // Fetch images and assignment for response
        List<NoveltyImage> images = noveltyImageRepository.findByNoveltyIdOrderByUploadedAtDesc(noveltyId);
        NoveltyAssignment assignment = noveltyAssignmentRepository.findLatestByNoveltyId(noveltyId).orElse(null);
        
        return mapToResponse(novelty, images, assignment);
    }

    /**
     * Close novelty after completion verification (Admin only)
     */
    public NoveltyResponse verifyResolution(Long noveltyId, boolean approved, String verificationNotes, Long verifiedByUserId) {
        // Find novelty
        Novelty novelty = noveltyRepository.findById(noveltyId)
                .orElseThrow(() -> new NoveltyOperationException("Novelty not found with id: " + noveltyId));

        // Validate admin can verify
        validationService.validateAdminCanVerify(verifiedByUserId);

        // Validate status
        if (novelty.getStatus() != NoveltyStatus.COMPLETADA) {
            throw new NoveltyOperationException("Only COMPLETADA novelties can be closed");
        }

        // Update status based on approval
        if (approved) {
            novelty.close();
        } else {
            // Rejected, back to EN_CURSO
            novelty.setStatus(NoveltyStatus.EN_CURSO);
            novelty.setCompletedAt(null); // Reset completion timestamp
        }

        if (verificationNotes != null) {
            novelty.setObservations(novelty.getObservations() + "\n\nNotas de verificación: " + verificationNotes);
        }
        novelty.setUpdatedAt(LocalDateTime.now());
        noveltyRepository.save(novelty);

        // Notify assigned crew if rejected
        if (!approved) {
            notificationService.notifyRejection(novelty);
        }

        // Fetch images and assignment for response
        List<NoveltyImage> images = noveltyImageRepository.findByNoveltyIdOrderByUploadedAtDesc(noveltyId);
        NoveltyAssignment assignment = noveltyAssignmentRepository.findLatestByNoveltyId(noveltyId).orElse(null);
        
        return mapToResponse(novelty, images, assignment);
    }

    /**
     * Cancel novelty (Admin only - only from CREADA status)
     */
    public NoveltyResponse cancelNovelty(Long noveltyId, String cancellationReason, Long cancelledByUserId) {
        // Find novelty
        Novelty novelty = noveltyRepository.findById(noveltyId)
                .orElseThrow(() -> new NoveltyOperationException("Novelty not found with id: " + noveltyId));

        // Validate admin can cancel
        validationService.validateAdminCanCancel(cancelledByUserId);

        // Validate status allows cancellation (only CREADA or EN_CURSO)
        if (novelty.getStatus() == NoveltyStatus.CERRADA || novelty.getStatus() == NoveltyStatus.COMPLETADA) {
            throw new NoveltyOperationException("Cannot cancel a CERRADA or COMPLETADA novelty");
        }

        // Update status using entity method
        novelty.cancel();
        if (cancellationReason != null) {
            novelty.setObservations(novelty.getObservations() + "\n\nMotivo de cancelación: " + cancellationReason);
        }
        novelty.setUpdatedAt(LocalDateTime.now());
        noveltyRepository.save(novelty);

        // Notify involved parties
        notificationService.notifyCancellation(novelty);

        // Fetch images and assignment for response
        List<NoveltyImage> images = noveltyImageRepository.findByNoveltyIdOrderByUploadedAtDesc(noveltyId);
        NoveltyAssignment assignment = noveltyAssignmentRepository.findLatestByNoveltyId(noveltyId).orElse(null);
        
        return mapToResponse(novelty, images, assignment);
    }

    /**
     * Update novelty status directly (for development/testing purposes).
     * 
     * WARNING: This bypasses normal workflow validations.
     * Use specific endpoints (assign, start, resolve) for production.
     * 
     * @param noveltyId Novelty ID
     * @param newStatus New status to set
     * @param notes Optional notes about the status change
     * @param updatedByUserId User making the change (optional for public access)
     * @return Updated novelty response
     */
    public NoveltyResponse updateStatus(Long noveltyId, NoveltyStatus newStatus, String notes, Long updatedByUserId) {
        log.info("Updating novelty {} status to {} by user {}", noveltyId, newStatus, updatedByUserId);
        
        // Find novelty
        Novelty novelty = noveltyRepository.findById(noveltyId)
                .orElseThrow(() -> new NoveltyOperationException("Novelty not found with id: " + noveltyId));

        NoveltyStatus oldStatus = novelty.getStatus();
        log.info("Changing novelty {} from {} to {}", noveltyId, oldStatus, newStatus);

        // Update status
        novelty.setStatus(newStatus);
        novelty.setUpdatedAt(LocalDateTime.now());
        
        // Update timestamps based on new status
        LocalDateTime now = LocalDateTime.now();
        switch (newStatus) {
            case EN_CURSO:
                // Novelty is now in progress
                break;
            case COMPLETADA:
                // Set completed timestamp
                if (novelty.getCompletedAt() == null) {
                    novelty.setCompletedAt(now);
                }
                break;
            case CERRADA:
                // Set both completed and closed timestamps
                if (novelty.getCompletedAt() == null) {
                    novelty.setCompletedAt(now);
                }
                if (novelty.getClosedAt() == null) {
                    novelty.setClosedAt(now);
                }
                break;
            case CANCELADA:
                // Keep existing cancel logic
                break;
            default:
                break;
        }
        
        // Append notes if provided
        if (notes != null && !notes.trim().isEmpty()) {
            String currentObs = novelty.getObservations() != null ? novelty.getObservations() : "";
            novelty.setObservations(currentObs + "\n\nCambio de estado (" + oldStatus + " → " + newStatus + "): " + notes);
        }
        
        noveltyRepository.save(novelty);
        log.info("Novelty {} status updated successfully from {} to {}", noveltyId, oldStatus, newStatus);

        // Fetch images and assignment for response
        List<NoveltyImage> images = noveltyImageRepository.findByNoveltyIdOrderByUploadedAtDesc(noveltyId);
        NoveltyAssignment assignment = noveltyAssignmentRepository.findLatestByNoveltyId(noveltyId).orElse(null);
        
        return mapToResponse(novelty, images, assignment);
    }

    /**
     * Get novelty by ID with full details
     */
    @Transactional(readOnly = true)
    public NoveltyDetailResponse getNoveltyById(Long noveltyId, Long userId) {
        // Find novelty
        Novelty novelty = noveltyRepository.findById(noveltyId)
                .orElseThrow(() -> new NoveltyOperationException("Novelty not found with id: " + noveltyId));

        // Validate user can view (skip validation if userId is null - public access)
        if (userId != null) {
            validationService.validateCanView(userId, novelty);
        }

        return mapToDetailResponse(novelty);
    }

    /**
     * Search novelties with pagination and filters
     */
    @Transactional(readOnly = true)
    public NoveltyPageResponse searchNovelties(NoveltySearchRequest request, Long userId) {
        // Build pageable
        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        // Convert status string to enum if present
        NoveltyStatus statusEnum = null;
        if (request.getStatus() != null) {
            try {
                statusEnum = NoveltyStatus.valueOf(request.getStatus().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new NoveltyOperationException("Invalid status: " + request.getStatus());
            }
        }

        // Get novelties based on user role
        Page<Novelty> noveltyPage = noveltyRepository.findByFilters(
                statusEnum,
                request.getReason(),
                request.getCrewId(),
                request.getCreatedBy(),
                request.getStartDate(),
                request.getEndDate(),
                pageable
        );

        // Map to response
        List<NoveltyResponse> novelties = noveltyPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        NoveltyPageResponse response = new NoveltyPageResponse();
        response.setNovelties(novelties);
        response.setTotalElements(noveltyPage.getTotalElements());
        response.setTotalPages(noveltyPage.getTotalPages());
        response.setCurrentPage(noveltyPage.getNumber());
        response.setPageSize(noveltyPage.getSize());

        return response;
    }

    /**
     * Get novelties by crew ID
     */
    @Transactional(readOnly = true)
    public List<NoveltyResponse> getNoveltyByCrew(Long crewId) {
        List<Novelty> novelties = noveltyRepository.findByCrewIdOrderByCreatedAtDesc(crewId);
        return novelties.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get novelties assigned to a user's active crew.
     * Returns empty list if user has no active crew membership.
     * 
     * @param userId User ID
     * @return List of novelties assigned to user's crew
     */
    @Transactional(readOnly = true)
    public List<NoveltyResponse> getNoveltiesByUser(Long userId) {
        log.debug("Getting novelties for user: {}", userId);
        
        // Find active crew membership for the user
        List<CrewMember> memberships = crewMemberRepository.findUserHistory(userId);
        Optional<CrewMember> activeMembership = memberships.stream()
                .filter(m -> m.getLeftAt() == null)
                .findFirst();
        
        if (activeMembership.isEmpty()) {
            log.debug("User {} has no active crew membership", userId);
            return Collections.emptyList();
        }
        
        Long crewId = activeMembership.get().getCrewId();
        log.debug("User {} belongs to active crew: {}", userId, crewId);
        return getNoveltyByCrew(crewId);
    }

    /**
     * Get novelties by status
     */
    @Transactional(readOnly = true)
    public List<NoveltyResponse> getNoveltyByStatus(NoveltyStatus status) {
        List<Novelty> novelties = noveltyRepository.findByStatusOrderByCreatedAtDesc(status);
        return novelties.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Private mapping methods

    private NoveltyResponse mapToResponse(Novelty novelty, List<NoveltyImage> images, NoveltyAssignment assignment) {
        NoveltyResponse response = new NoveltyResponse();
        response.setId(novelty.getId());
        response.setAreaId(novelty.getAreaId());
        response.setReason(novelty.getReason());
        response.setAccountNumber(novelty.getAccountNumber());
        response.setMeterNumber(novelty.getMeterNumber());
        response.setActiveReading(novelty.getActiveReading());
        response.setReactiveReading(novelty.getReactiveReading());
        response.setMunicipality(novelty.getMunicipality());
        response.setAddress(novelty.getAddress());
        response.setDescription(novelty.getDescription());
        response.setObservations(novelty.getObservations());
        response.setStatus(novelty.getStatus());
        response.setCreatedBy(novelty.getCreatedBy());
        response.setCrewId(novelty.getCrewId());
        response.setCreatedAt(novelty.getCreatedAt());
        response.setUpdatedAt(novelty.getUpdatedAt());
        response.setCompletedAt(novelty.getCompletedAt());
        response.setClosedAt(novelty.getClosedAt());
        response.setCancelledAt(novelty.getCancelledAt());
        response.setImageCount(images.size());
        response.setHasAssignment(assignment != null);
        return response;
    }
    
    private NoveltyResponse mapToResponse(Novelty novelty) {
        List<NoveltyImage> images = noveltyImageRepository.findByNoveltyIdOrderByUploadedAtDesc(novelty.getId());
        NoveltyAssignment assignment = noveltyAssignmentRepository.findLatestByNoveltyId(novelty.getId()).orElse(null);
        return mapToResponse(novelty, images, assignment);
    }

    private NoveltyDetailResponse mapToDetailResponse(Novelty novelty) {
        NoveltyDetailResponse response = new NoveltyDetailResponse();
        response.setId(novelty.getId());
        response.setAreaId(novelty.getAreaId());
        response.setReason(novelty.getReason());
        response.setAccountNumber(novelty.getAccountNumber());
        response.setMeterNumber(novelty.getMeterNumber());
        response.setActiveReading(novelty.getActiveReading());
        response.setReactiveReading(novelty.getReactiveReading());
        response.setMunicipality(novelty.getMunicipality());
        response.setAddress(novelty.getAddress());
        response.setDescription(novelty.getDescription());
        response.setObservations(novelty.getObservations());
        response.setStatus(novelty.getStatus());
        response.setCreatedBy(novelty.getCreatedBy());
        response.setCrewId(novelty.getCrewId());
        response.setCreatedAt(novelty.getCreatedAt());
        response.setUpdatedAt(novelty.getUpdatedAt());
        response.setCompletedAt(novelty.getCompletedAt());
        response.setClosedAt(novelty.getClosedAt());
        response.setCancelledAt(novelty.getCancelledAt());

        // Load images
        List<NoveltyImage> images = noveltyImageRepository.findByNoveltyIdOrderByUploadedAtDesc(novelty.getId());
        List<NoveltyDetailResponse.ImageDetail> imageDetails = images.stream()
                .map(img -> {
                    NoveltyDetailResponse.ImageDetail detail = new NoveltyDetailResponse.ImageDetail();
                    detail.setId(img.getId());
                    detail.setImageUrl(img.getImageUrl());
                    detail.setUploadedByUserId(img.getUploadedByUserId());
                    detail.setUploadedAt(img.getUploadedAt());
                    return detail;
                })
                .collect(Collectors.toList());
        response.setImages(imageDetails);

        // Load assignment if exists
        noveltyAssignmentRepository.findLatestByNoveltyId(novelty.getId())
                .ifPresent(response::setAssignment);

        return response;
    }
}
