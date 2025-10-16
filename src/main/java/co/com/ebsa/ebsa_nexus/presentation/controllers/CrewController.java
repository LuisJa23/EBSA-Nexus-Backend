package co.com.ebsa.ebsa_nexus.presentation.controllers;

import co.com.ebsa.ebsa_nexus.application.crew.services.CrewService;
import co.com.ebsa.ebsa_nexus.application.crew.services.IncidentAssignmentService;
import co.com.ebsa.ebsa_nexus.domain.crew.entities.Crew;
import co.com.ebsa.ebsa_nexus.domain.crew.enums.CrewStatus;
import co.com.ebsa.ebsa_nexus.presentation.dto.request.ChangeCrewStatusRequest;
import co.com.ebsa.ebsa_nexus.presentation.dto.request.CreateCrewRequest;
import co.com.ebsa.ebsa_nexus.presentation.dto.request.UpdateCrewRequest;
import co.com.ebsa.ebsa_nexus.presentation.dto.response.ApiResponse;
import co.com.ebsa.ebsa_nexus.presentation.dto.response.CrewResponse;
import co.com.ebsa.ebsa_nexus.presentation.mappers.CrewDtoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for crew management operations
 * Provides CRUD endpoints for crews
 * 
 * Base path: /api/crews
 */
@Slf4j
@RestController
@RequestMapping("/api/crews")
@RequiredArgsConstructor
public class CrewController {
    
    private final CrewService crewService;
    private final IncidentAssignmentService assignmentService;
    private final CrewDtoMapper crewMapper;
    
    /**
     * Create a new crew
     * POST /api/crews
     * 
     * @param request CreateCrewRequest with crew information
     * @return Created crew response with HTTP 201
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CrewResponse>> createCrew(@Valid @RequestBody CreateCrewRequest request) {
        log.info("Creating new crew: {}", request.getName());
        
        Crew crew = crewService.createCrew(
            request.getName(),
            request.getDescription(),
            request.getCreatedBy()
        );
        
        CrewResponse response = crewMapper.toResponse(crew);
        
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("Crew created successfully", response));
    }
    
    /**
     * Get all active crews
     * GET /api/crews
     * 
     * @return List of all active crews
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CrewResponse>>> getAllCrews() {
        log.info("Fetching all active crews");
        
        List<Crew> crews = crewService.getAllActiveCrews();
        List<CrewResponse> responses = crews.stream()
            .map(crewMapper::toResponse)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(
            ApiResponse.success("Crews retrieved successfully", responses)
        );
    }
    
    /**
     * Get crew by ID
     * GET /api/crews/{id}
     * 
     * @param id Crew ID
     * @return Crew information
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CrewResponse>> getCrewById(@PathVariable Long id) {
        log.info("Fetching crew with ID: {}", id);
        
        Crew crew = crewService.getCrewById(id);
        
        // Get additional statistics
        int memberCount = crewService.countActiveCrews() > 0 ? 0 : 0; // Placeholder
        boolean hasAssignments = assignmentService.hasOpenAssignments(id);
        
        CrewResponse response = crewMapper.toResponse(crew, memberCount, hasAssignments);
        
        return ResponseEntity.ok(
            ApiResponse.success("Crew retrieved successfully", response)
        );
    }
    
    /**
     * Update crew information
     * PUT /api/crews/{id}
     * 
     * @param id Crew ID
     * @param request UpdateCrewRequest with new information
     * @return Updated crew response
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CrewResponse>> updateCrew(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCrewRequest request) {
        
        log.info("Updating crew with ID: {}", id);
        
        Crew crew = crewService.updateCrewInfo(id, request.getName(), request.getDescription());
        CrewResponse response = crewMapper.toResponse(crew);
        
        return ResponseEntity.ok(
            ApiResponse.success("Crew updated successfully", response)
        );
    }
    
    /**
     * Change crew status
     * PATCH /api/crews/{id}/status
     * 
     * @param id Crew ID
     * @param request ChangeCrewStatusRequest with new status
     * @return Updated crew response
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<CrewResponse>> changeCrewStatus(
            @PathVariable Long id,
            @Valid @RequestBody ChangeCrewStatusRequest request) {
        
        log.info("Changing status of crew {} to {}", id, request.getNewStatus());
        
        Crew crew = crewService.changeCrewStatus(id, request.getNewStatus());
        CrewResponse response = crewMapper.toResponse(crew);
        
        return ResponseEntity.ok(
            ApiResponse.success("Crew status changed successfully", response)
        );
    }
    
    /**
     * Mark crew as available
     * PATCH /api/crews/{id}/mark-available
     * 
     * @param id Crew ID
     * @return Updated crew response
     */
    @PatchMapping("/{id}/mark-available")
    public ResponseEntity<ApiResponse<CrewResponse>> markAsAvailable(@PathVariable Long id) {
        log.info("Marking crew {} as available", id);
        
        Crew crew = crewService.markAsAvailable(id);
        CrewResponse response = crewMapper.toResponse(crew);
        
        return ResponseEntity.ok(
            ApiResponse.success("Crew marked as available", response)
        );
    }
    
    /**
     * Mark crew as in attention
     * PATCH /api/crews/{id}/mark-in-attention
     * 
     * @param id Crew ID
     * @return Updated crew response
     */
    @PatchMapping("/{id}/mark-in-attention")
    public ResponseEntity<ApiResponse<CrewResponse>> markAsInAttention(@PathVariable Long id) {
        log.info("Marking crew {} as in attention", id);
        
        Crew crew = crewService.markAsInAttention(id);
        CrewResponse response = crewMapper.toResponse(crew);
        
        return ResponseEntity.ok(
            ApiResponse.success("Crew marked as in attention", response)
        );
    }
    
    /**
     * Soft delete a crew
     * DELETE /api/crews/{id}
     * 
     * @param id Crew ID
     * @return Success response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCrew(@PathVariable Long id) {
        log.info("Deleting crew with ID: {}", id);
        
        crewService.deleteCrew(id);
        
        return ResponseEntity.ok(
            ApiResponse.success("Crew deleted successfully")
        );
    }
    
    /**
     * Get crews by status
     * GET /api/crews/status/{status}
     * 
     * @param status Crew status (DISPONIBLE, EN_ATENCION, INACTIVO)
     * @return List of crews with specified status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<CrewResponse>>> getCrewsByStatus(@PathVariable CrewStatus status) {
        log.info("Fetching crews with status: {}", status);
        
        List<Crew> crews = crewService.getCrewsByStatus(status);
        List<CrewResponse> responses = crews.stream()
            .map(crewMapper::toResponse)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(
            ApiResponse.success("Crews retrieved successfully", responses)
        );
    }
    
    /**
     * Get available crews (status = DISPONIBLE)
     * GET /api/crews/available
     * 
     * @return List of available crews
     */
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<CrewResponse>>> getAvailableCrews() {
        log.info("Fetching available crews");
        
        List<Crew> crews = crewService.getAvailableCrews();
        List<CrewResponse> responses = crews.stream()
            .map(crewMapper::toResponse)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(
            ApiResponse.success("Available crews retrieved successfully", responses)
        );
    }
    
    /**
     * Get crews created by a specific user
     * GET /api/crews/created-by/{userId}
     * 
     * @param userId User ID
     * @return List of crews created by the user
     */
    @GetMapping("/created-by/{userId}")
    public ResponseEntity<ApiResponse<List<CrewResponse>>> getCrewsCreatedBy(@PathVariable Long userId) {
        log.info("Fetching crews created by user: {}", userId);
        
        List<Crew> crews = crewService.getCrewsCreatedBy(userId);
        List<CrewResponse> responses = crews.stream()
            .map(crewMapper::toResponse)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(
            ApiResponse.success("Crews retrieved successfully", responses)
        );
    }
    
    /**
     * Get total count of active crews
     * GET /api/crews/count
     * 
     * @return Count of active crews
     */
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> countActiveCrews() {
        log.info("Counting active crews");
        
        long count = crewService.countActiveCrews();
        
        return ResponseEntity.ok(
            ApiResponse.success("Count retrieved successfully", count)
        );
    }
    
    /**
     * Get count of crews by status
     * GET /api/crews/count/status/{status}
     * 
     * @param status Crew status
     * @return Count of crews with specified status
     */
    @GetMapping("/count/status/{status}")
    public ResponseEntity<ApiResponse<Long>> countCrewsByStatus(@PathVariable CrewStatus status) {
        log.info("Counting crews with status: {}", status);
        
        long count = crewService.countCrewsByStatus(status);
        
        return ResponseEntity.ok(
            ApiResponse.success("Count retrieved successfully", count)
        );
    }
}
