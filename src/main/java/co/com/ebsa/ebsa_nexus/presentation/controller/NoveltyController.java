package co.com.ebsa.ebsa_nexus.presentation.controller;

import co.com.ebsa.ebsa_nexus.application.dto.request.novelty.AssignCrewRequest;
import co.com.ebsa.ebsa_nexus.application.dto.request.novelty.CreateNoveltyRequest;
import co.com.ebsa.ebsa_nexus.application.dto.request.novelty.NoveltySearchRequest;
import co.com.ebsa.ebsa_nexus.application.dto.request.novelty.ResolveNoveltyRequest;
import co.com.ebsa.ebsa_nexus.application.dto.response.NoveltyDetailResponse;
import co.com.ebsa.ebsa_nexus.application.dto.response.NoveltyPageResponse;
import co.com.ebsa.ebsa_nexus.application.dto.response.NoveltyResponse;
import co.com.ebsa.ebsa_nexus.application.service.novelty.NoveltyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Novelty Management System.
 * Handles all novelty-related operations: creation, assignment, status updates, and queries.
 * 
 * @author EBSA Nexus Team
 * @version 1.0
 * @since 2025-10-21
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/novelties")
@RequiredArgsConstructor
public class NoveltyController {

    private final NoveltyService noveltyService;

    /**
     * Create a new novelty (Any authenticated user).
     * Accepts multipart/form-data with JSON fields + image files
     * 
     * @param request Novelty creation data with images
     * @param userId User ID from authentication context
     * @return Created novelty details
     */
    @PostMapping(consumes = {"multipart/form-data", "application/json"})
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<NoveltyResponse> createNovelty(
            @Valid @ModelAttribute CreateNoveltyRequest request,
            @RequestAttribute("userId") Long userId) {
        
        NoveltyResponse response = noveltyService.createNovelty(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Assign crew to resolve a novelty (Admin only).
     * 
     * @param noveltyId Novelty ID
     * @param request Assignment details
     * @param userId User ID from authentication context
     * @return Updated novelty details
     */
    @PostMapping("/{noveltyId}/assign")
    // @PreAuthorize("hasRole('ADMIN')") // Comentado temporalmente para pruebas sin autenticación
    public ResponseEntity<NoveltyResponse> assignCrew(
            @PathVariable Long noveltyId,
            @Valid @RequestBody AssignCrewRequest request,
            @RequestAttribute(value = "userId", required = false) Long userId) {
        
        NoveltyResponse response = noveltyService.assignCrew(noveltyId, request, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Start working on novelty (Assigned crew only).
     * 
     * @param noveltyId Novelty ID
     * @param userId User ID from authentication context
     * @return Updated novelty details
     */
    @PutMapping("/{noveltyId}/start")
    @PreAuthorize("hasAnyRole('TRABAJADOR', 'LIDER_CUADRILLA')")
    public ResponseEntity<NoveltyResponse> startProgress(
            @PathVariable Long noveltyId,
            @RequestAttribute("userId") Long userId) {
        
        NoveltyResponse response = noveltyService.startProgress(noveltyId, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Mark novelty as resolved (Assigned crew only).
     * 
     * @param noveltyId Novelty ID
     * @param request Resolution notes request
     * @param userId User ID from authentication context
     * @return Updated novelty details
     */
    @PutMapping("/{noveltyId}/resolve")
    @PreAuthorize("hasAnyRole('TRABAJADOR', 'LIDER_CUADRILLA')")
    public ResponseEntity<NoveltyResponse> resolveNovelty(
            @PathVariable Long noveltyId,
            @Valid @RequestBody ResolveNoveltyRequest request,
            @RequestAttribute("userId") Long userId) {
        
        NoveltyResponse response = noveltyService.resolveNovelty(noveltyId, request.getResolutionNotes(), userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Verify novelty resolution (Admin only).
     * 
     * @param noveltyId Novelty ID
     * @param approved Whether resolution is approved
     * @param verificationNotes Verification notes
     * @param userId User ID from authentication context
     * @return Updated novelty details
     */
    @PutMapping("/{noveltyId}/verify")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NoveltyResponse> verifyResolution(
            @PathVariable Long noveltyId,
            @RequestParam boolean approved,
            @RequestParam(required = false) String verificationNotes,
            @RequestAttribute("userId") Long userId) {
        
        NoveltyResponse response = noveltyService.verifyResolution(noveltyId, approved, verificationNotes, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Cancel novelty (Admin only).
     * 
     * @param noveltyId Novelty ID
     * @param cancellationReason Reason for cancellation
     * @param userId User ID from authentication context
     * @return Updated novelty details
     */
    @PutMapping("/{noveltyId}/cancel")
    @PreAuthorize("hasRole('ADMIN, TRABAJADOR, JEFE_AREA')")
    public ResponseEntity<NoveltyResponse> cancelNovelty(
            @PathVariable Long noveltyId,
            @RequestParam String cancellationReason,
            @RequestAttribute("userId") Long userId) {
        
        NoveltyResponse response = noveltyService.cancelNovelty(noveltyId, cancellationReason, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Update novelty status directly (for development/testing).
     * 
     * PATCH /api/v1/novelties/{id}/status
     * Body: {"status": "EN_CURSO", "notes": "Optional notes"}
     * 
     * WARNING: This bypasses normal workflow validations.
     * Use specific endpoints for production workflows.
     * 
     * @param noveltyId Novelty ID
     * @param request Status update request
     * @param userId User ID from authentication context (optional)
     * @return Updated novelty details
     */
    @PatchMapping("/{noveltyId}/status")
    // Public access for development/testing
    public ResponseEntity<NoveltyResponse> updateNoveltyStatus(
            @PathVariable Long noveltyId,
            @Valid @RequestBody co.com.ebsa.ebsa_nexus.application.dto.request.novelty.UpdateNoveltyStatusRequest request,
            @RequestAttribute(value = "userId", required = false) Long userId) {
        
        log.info("PATCH /api/v1/novelties/{}/status - New status: {}", noveltyId, request.getStatus());
        
        NoveltyResponse response = noveltyService.updateStatus(
            noveltyId, 
            request.getStatus(), 
            request.getNotes(),
            userId != null ? userId : 1L // Default to user 1 for development
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get novelty by ID with full details.
     * 
     * @param noveltyId Novelty ID
     * @param userId User ID from authentication context (optional for public access)
     * @return Novelty details
     */
    @GetMapping("/{noveltyId}")
    // @PreAuthorize("hasAnyRole('SUPERVISOR', 'TRABAJADOR', 'LIDER_CUADRILLA', 'ADMIN')") // Comentado temporalmente para pruebas
    public ResponseEntity<NoveltyDetailResponse> getNoveltyById(
            @PathVariable Long noveltyId,
            @RequestAttribute(value = "userId", required = false) Long userId) {
        
        NoveltyDetailResponse response = noveltyService.getNoveltyById(noveltyId, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Search novelties with filters and pagination.
     * 
     * @param request Search filters
     * @param userId User ID from authentication context (optional for public access)
     * @return Paginated novelty list
     */
    @GetMapping("/search")
    // @PreAuthorize("hasAnyRole('SUPERVISOR', 'TRABAJADOR', 'LIDER_CUADRILLA', 'ADMIN')") // Comentado temporalmente para pruebas
    public ResponseEntity<NoveltyPageResponse> searchNovelties(
            @ModelAttribute NoveltySearchRequest request,
            @RequestAttribute(value = "userId", required = false) Long userId) {
        
        NoveltyPageResponse response = noveltyService.searchNovelties(request, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get novelties by crew ID.
     * 
     * @param crewId Crew ID
     * @return List of novelties for the crew
     */
    @GetMapping("/crew/{crewId}")
    // @PreAuthorize("hasAnyRole('TRABAJADOR', 'LIDER_CUADRILLA', 'SUPERVISOR', 'ADMIN')") // Comentado temporalmente para pruebas
    public ResponseEntity<java.util.List<NoveltyResponse>> getNoveltyByCrew(
            @PathVariable Long crewId) {
        
        java.util.List<NoveltyResponse> response = noveltyService.getNoveltyByCrew(crewId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get novelties assigned to a user's active crew.
     * Returns empty list if user has no active crew membership.
     * 
     * @param userId User ID
     * @return List of novelties assigned to the user's crew
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('TRABAJADOR', 'LIDER_CUADRILLA', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<java.util.List<NoveltyResponse>> getNoveltiesByUser(
            @PathVariable Long userId) {
        
        log.info("Fetching novelties for user: {}", userId);
        java.util.List<NoveltyResponse> response = noveltyService.getNoveltiesByUser(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get novelties by status.
     * 
     * @param status Novelty status
     * @return List of novelties with the specified status
     */
    @GetMapping("/status/{status}")
    // @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')") // Comentado temporalmente para pruebas
    public ResponseEntity<java.util.List<NoveltyResponse>> getNoveltyByStatus(
            @PathVariable co.com.ebsa.ebsa_nexus.domain.enums.NoveltyStatus status) {
        
        java.util.List<NoveltyResponse> response = noveltyService.getNoveltyByStatus(status);
        return ResponseEntity.ok(response);
    }
}
