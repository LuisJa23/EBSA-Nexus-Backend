package co.com.ebsa.ebsa_nexus.application.service.novelty;

import co.com.ebsa.ebsa_nexus.application.dto.request.novelty.AssignCrewRequest;
import co.com.ebsa.ebsa_nexus.application.dto.request.novelty.CreateNoveltyRequest;
import co.com.ebsa.ebsa_nexus.application.dto.request.novelty.NoveltySearchRequest;
import co.com.ebsa.ebsa_nexus.application.dto.request.novelty.UploadImagesRequest;
import co.com.ebsa.ebsa_nexus.application.dto.response.NoveltyDetailResponse;
import co.com.ebsa.ebsa_nexus.application.dto.response.NoveltyPageResponse;
import co.com.ebsa.ebsa_nexus.application.dto.response.NoveltyResponse;
import co.com.ebsa.ebsa_nexus.domain.entity.*;
import co.com.ebsa.ebsa_nexus.domain.enums.NoveltyStatus;
import co.com.ebsa.ebsa_nexus.domain.exception.novelty.NoveltyOperationException;
import co.com.ebsa.ebsa_nexus.domain.repository.*;
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
import java.util.stream.Collectors;

@Service
@Transactional
public class NoveltyService {

    private final NoveltyRepository noveltyRepository;
    private final NoveltyImageRepository noveltyImageRepository;
    private final NoveltyAssignmentRepository noveltyAssignmentRepository;
    private final NoveltyValidationService validationService;
    private final NoveltyNotificationService notificationService;

    public NoveltyService(
            NoveltyRepository noveltyRepository,
            NoveltyImageRepository noveltyImageRepository,
            NoveltyAssignmentRepository noveltyAssignmentRepository,
            NoveltyValidationService validationService,
            NoveltyNotificationService notificationService) {
        this.noveltyRepository = noveltyRepository;
        this.noveltyImageRepository = noveltyImageRepository;
        this.noveltyAssignmentRepository = noveltyAssignmentRepository;
        this.validationService = validationService;
        this.notificationService = notificationService;
    }

    /**
     * Create a new novelty from supervisor report
     * The novelty is created in REPORTED status without crew assignment
     */
    public NoveltyResponse createNovelty(CreateNoveltyRequest request, Long reportedByUserId) {
        // Validate supervisor can report novelty
        validationService.validateSupervisorCanReport(reportedByUserId);

        // Create novelty entity in REPORTED status (no crew assigned yet)
        Novelty novelty = new Novelty();
        novelty.setReason(request.getReason());
        novelty.setDescription(request.getDescription());
        novelty.setLocation(request.getLocation());
        novelty.setReportedByUserId(reportedByUserId);
        novelty.setStatus(NoveltyStatus.REPORTED);
        novelty.setReportedAt(LocalDateTime.now());
        novelty.setCreatedAt(LocalDateTime.now());
        novelty.setUpdatedAt(LocalDateTime.now());

        // Save novelty
        Novelty savedNovelty = noveltyRepository.save(novelty);

        // Send notification to admin
        notificationService.notifyNewNovelty(savedNovelty);

        return mapToResponse(savedNovelty, Collections.emptyList(), null);
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
        assignment.setAssignedByUserId(assignedByUserId);
        assignment.setInstructions(request.getInstructions());
        assignment.setPriority(request.getPriority());
        assignment.setEstimatedResolutionDate(request.getEstimatedResolutionDate());
        assignment.setAssignedAt(LocalDateTime.now());
        noveltyAssignmentRepository.save(assignment);

        // Update novelty status
        novelty.setStatus(NoveltyStatus.ASSIGNED);
        novelty.setUpdatedAt(LocalDateTime.now());
        noveltyRepository.save(novelty);

        // Send notification to assigned crew
        notificationService.notifyCrewAssignment(novelty, assignment);

        // Fetch images and assignment for response
        List<NoveltyImage> images = noveltyImageRepository.findByNoveltyIdOrderByUploadedAtDesc(noveltyId);
        
        return mapToResponse(novelty, images, assignment);
    }

    /**
     * Mark novelty as in progress
     */
    public NoveltyResponse startProgress(Long noveltyId, Long userId) {
        // Find novelty
        Novelty novelty = noveltyRepository.findById(noveltyId)
                .orElseThrow(() -> new NoveltyOperationException("Novelty not found with id: " + noveltyId));

        // Validate user is assigned crew
        validationService.validateUserIsAssignedCrew(userId, novelty);

        // Validate status transition
        if (novelty.getStatus() != NoveltyStatus.ASSIGNED) {
            throw new NoveltyOperationException("Novelty must be in ASSIGNED status to start progress");
        }

        // Update status
        novelty.setStatus(NoveltyStatus.IN_PROGRESS);
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
     * Mark novelty as resolved with completion details
     */
    public NoveltyResponse resolveNovelty(Long noveltyId, String resolutionNotes, Long resolvedByUserId) {
        // Find novelty
        Novelty novelty = noveltyRepository.findById(noveltyId)
                .orElseThrow(() -> new NoveltyOperationException("Novelty not found with id: " + noveltyId));

        // Validate user can resolve
        validationService.validateCanResolve(resolvedByUserId, novelty);

        // Validate status allows resolution
        if (novelty.getStatus() != NoveltyStatus.IN_PROGRESS && novelty.getStatus() != NoveltyStatus.ASSIGNED) {
            throw new NoveltyOperationException("Novelty must be in IN_PROGRESS or ASSIGNED status to be resolved");
        }

        // Update novelty
        novelty.setStatus(NoveltyStatus.RESOLVED);
        novelty.setResolutionNotes(resolutionNotes);
        novelty.setResolvedByUserId(resolvedByUserId);
        novelty.setResolvedAt(LocalDateTime.now());
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
     * Verify novelty resolution (Admin only)
     */
    public NoveltyResponse verifyResolution(Long noveltyId, boolean approved, String verificationNotes, Long verifiedByUserId) {
        // Find novelty
        Novelty novelty = noveltyRepository.findById(noveltyId)
                .orElseThrow(() -> new NoveltyOperationException("Novelty not found with id: " + noveltyId));

        // Validate admin can verify
        validationService.validateAdminCanVerify(verifiedByUserId);

        // Validate status
        if (novelty.getStatus() != NoveltyStatus.RESOLVED) {
            throw new NoveltyOperationException("Only RESOLVED novelties can be verified");
        }

        // Update status based on approval
        if (approved) {
            novelty.setStatus(NoveltyStatus.CLOSED);
        } else {
            novelty.setStatus(NoveltyStatus.IN_PROGRESS); // Rejected, back to in progress
        }

        novelty.setVerifiedByUserId(verifiedByUserId);
        novelty.setVerificationNotes(verificationNotes);
        novelty.setVerifiedAt(LocalDateTime.now());
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
     * Cancel novelty (Admin only)
     */
    public NoveltyResponse cancelNovelty(Long noveltyId, String cancellationReason, Long cancelledByUserId) {
        // Find novelty
        Novelty novelty = noveltyRepository.findById(noveltyId)
                .orElseThrow(() -> new NoveltyOperationException("Novelty not found with id: " + noveltyId));

        // Validate admin can cancel
        validationService.validateAdminCanCancel(cancelledByUserId);

        // Validate status allows cancellation
        if (novelty.getStatus() == NoveltyStatus.CLOSED) {
            throw new NoveltyOperationException("Cannot cancel a CLOSED novelty");
        }

        // Update status
        novelty.setStatus(NoveltyStatus.CANCELLED);
        novelty.setCancellationReason(cancellationReason);
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
     * Get novelty by ID with full details
     */
    @Transactional(readOnly = true)
    public NoveltyDetailResponse getNoveltyById(Long noveltyId, Long userId) {
        // Find novelty
        Novelty novelty = noveltyRepository.findById(noveltyId)
                .orElseThrow(() -> new NoveltyOperationException("Novelty not found with id: " + noveltyId));

        // Validate user can view
        validationService.validateCanView(userId, novelty);

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
                Sort.by(Sort.Direction.DESC, "reportedAt")
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
                request.getReportedByUserId(),
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
        List<Novelty> novelties = noveltyRepository.findByCrewIdOrderByReportedAtDesc(crewId);
        return novelties.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get novelties by status
     */
    @Transactional(readOnly = true)
    public List<NoveltyResponse> getNoveltyByStatus(NoveltyStatus status) {
        List<Novelty> novelties = noveltyRepository.findByStatusOrderByReportedAtDesc(status);
        return novelties.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Private mapping methods

    private NoveltyResponse mapToResponse(Novelty novelty, List<NoveltyImage> images, NoveltyAssignment assignment) {
        NoveltyResponse response = new NoveltyResponse();
        response.setId(novelty.getId());
        response.setCrewId(novelty.getCrewId());
        response.setReason(novelty.getReason());
        response.setDescription(novelty.getDescription());
        response.setLocation(novelty.getLocation());
        response.setStatus(novelty.getStatus());
        response.setReportedByUserId(novelty.getReportedByUserId());
        response.setReportedAt(novelty.getReportedAt());
        response.setResolvedAt(novelty.getResolvedAt());
        response.setCreatedAt(novelty.getCreatedAt());
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
        response.setCrewId(novelty.getCrewId());
        response.setReason(novelty.getReason());
        response.setDescription(novelty.getDescription());
        response.setLocation(novelty.getLocation());
        response.setStatus(novelty.getStatus());
        response.setReportedByUserId(novelty.getReportedByUserId());
        response.setReportedAt(novelty.getReportedAt());
        response.setResolvedAt(novelty.getResolvedAt());
        response.setResolvedByUserId(novelty.getResolvedByUserId());
        response.setResolutionNotes(novelty.getResolutionNotes());
        response.setVerifiedByUserId(novelty.getVerifiedByUserId());
        response.setVerificationNotes(novelty.getVerificationNotes());
        response.setVerifiedAt(novelty.getVerifiedAt());
        response.setCancellationReason(novelty.getCancellationReason());
        response.setCreatedAt(novelty.getCreatedAt());
        response.setUpdatedAt(novelty.getUpdatedAt());

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
