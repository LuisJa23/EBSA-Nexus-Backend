package co.com.ebsa.ebsa_nexus.presentation.controllers;

import co.com.ebsa.ebsa_nexus.application.crew.services.IncidentAssignmentService;
import co.com.ebsa.ebsa_nexus.domain.crew.entities.IncidentAssignment;
import co.com.ebsa.ebsa_nexus.domain.crew.enums.AssignmentStatus;
import co.com.ebsa.ebsa_nexus.presentation.dto.request.AddNotesRequest;
import co.com.ebsa.ebsa_nexus.presentation.dto.request.AssignIncidentRequest;
import co.com.ebsa.ebsa_nexus.presentation.dto.request.CancelAssignmentRequest;
import co.com.ebsa.ebsa_nexus.presentation.dto.request.CompleteAssignmentRequest;
import co.com.ebsa.ebsa_nexus.presentation.dto.response.ApiResponse;
import co.com.ebsa.ebsa_nexus.presentation.dto.response.IncidentAssignmentResponse;
import co.com.ebsa.ebsa_nexus.presentation.mappers.IncidentAssignmentDtoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for incident assignment management operations
 * Provides endpoints for assigning incidents to crews and managing their lifecycle
 * 
 * Base path: /api/assignments
 */
@Slf4j
@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class IncidentAssignmentController {
    
    private final IncidentAssignmentService assignmentService;
    private final IncidentAssignmentDtoMapper assignmentMapper;
    
    /**
     * Assign an incident to a crew
     * POST /api/assignments
     * 
     * @param request AssignIncidentRequest with assignment information
     * @return Created assignment response with HTTP 201
     */
    @PostMapping
    public ResponseEntity<ApiResponse<IncidentAssignmentResponse>> assignIncident(
            @Valid @RequestBody AssignIncidentRequest request) {
        
        log.info("Assigning incident {} to crew {}", request.getIncidentId(), request.getCrewId());
        
        IncidentAssignment assignment;
        if (request.getNotes() != null && !request.getNotes().isEmpty()) {
            assignment = assignmentService.assignIncidentWithNotes(
                request.getCrewId(),
                request.getIncidentId(),
                request.getAssignedBy(),
                request.getNotes()
            );
        } else {
            assignment = assignmentService.assignIncident(
                request.getCrewId(),
                request.getIncidentId(),
                request.getAssignedBy()
            );
        }
        
        IncidentAssignmentResponse response = assignmentMapper.toResponse(assignment);
        
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("Incident assigned successfully", response));
    }
    
    /**
     * Get assignment by ID
     * GET /api/assignments/{id}
     * 
     * @param id Assignment ID
     * @return Assignment information
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<IncidentAssignmentResponse>> getAssignmentById(@PathVariable Long id) {
        log.info("Fetching assignment with ID: {}", id);
        
        IncidentAssignment assignment = assignmentService.getAssignmentById(id);
        IncidentAssignmentResponse response = assignmentMapper.toResponse(assignment);
        
        return ResponseEntity.ok(
            ApiResponse.success("Assignment retrieved successfully", response)
        );
    }
    
    /**
     * Get assignments for a specific crew
     * GET /api/assignments/crew/{crewId}
     * 
     * @param crewId Crew ID
     * @return List of assignments for the crew
     */
    @GetMapping("/crew/{crewId}")
    public ResponseEntity<ApiResponse<List<IncidentAssignmentResponse>>> getAssignmentsByCrew(
            @PathVariable Long crewId) {
        
        log.info("Fetching assignments for crew: {}", crewId);
        
        List<IncidentAssignment> assignments = assignmentService.getAssignmentsByCrew(crewId);
        List<IncidentAssignmentResponse> responses = assignments.stream()
            .map(assignmentMapper::toResponse)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(
            ApiResponse.success("Crew assignments retrieved successfully", responses)
        );
    }
    
    /**
     * Get active assignments for a specific crew
     * GET /api/assignments/crew/{crewId}/active
     * 
     * @param crewId Crew ID
     * @return List of active assignments for the crew
     */
    @GetMapping("/crew/{crewId}/active")
    public ResponseEntity<ApiResponse<List<IncidentAssignmentResponse>>> getActiveAssignments(
            @PathVariable Long crewId) {
        
        log.info("Fetching active assignments for crew: {}", crewId);
        
        List<IncidentAssignment> assignments = assignmentService.getActiveAssignments(crewId);
        List<IncidentAssignmentResponse> responses = assignments.stream()
            .map(assignmentMapper::toResponse)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(
            ApiResponse.success("Active assignments retrieved successfully", responses)
        );
    }
    
    /**
     * Get assignments for a specific incident
     * GET /api/assignments/incident/{incidentId}
     * 
     * @param incidentId Incident ID
     * @return List of assignments for the incident
     */
    @GetMapping("/incident/{incidentId}")
    public ResponseEntity<ApiResponse<List<IncidentAssignmentResponse>>> getAssignmentsByIncident(
            @PathVariable Long incidentId) {
        
        log.info("Fetching assignments for incident: {}", incidentId);
        
        List<IncidentAssignment> assignments = assignmentService.getAssignmentsByIncident(incidentId);
        List<IncidentAssignmentResponse> responses = assignments.stream()
            .map(assignmentMapper::toResponse)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(
            ApiResponse.success("Incident assignments retrieved successfully", responses)
        );
    }
    
    /**
     * Get the active assignment for a specific incident
     * GET /api/assignments/incident/{incidentId}/active
     * 
     * @param incidentId Incident ID
     * @return Active assignment for the incident (if exists)
     */
    @GetMapping("/incident/{incidentId}/active")
    public ResponseEntity<ApiResponse<IncidentAssignmentResponse>> getActiveAssignmentByIncident(
            @PathVariable Long incidentId) {
        
        log.info("Fetching active assignment for incident: {}", incidentId);
        
        IncidentAssignment assignment = assignmentService.getActiveAssignmentByIncident(incidentId);
        
        if (assignment == null) {
            return ResponseEntity.ok(
                ApiResponse.success("No active assignment found for this incident", null)
            );
        }
        
        IncidentAssignmentResponse response = assignmentMapper.toResponse(assignment);
        
        return ResponseEntity.ok(
            ApiResponse.success("Active assignment retrieved successfully", response)
        );
    }
    
    /**
     * Get assignments by status
     * GET /api/assignments/status/{status}
     * 
     * @param status Assignment status (ASIGNADO, EN_CURSO, COMPLETADO, CANCELADO)
     * @return List of assignments with specified status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<IncidentAssignmentResponse>>> getAssignmentsByStatus(
            @PathVariable AssignmentStatus status) {
        
        log.info("Fetching assignments with status: {}", status);
        
        List<IncidentAssignment> assignments = assignmentService.getAssignmentsByStatus(status);
        List<IncidentAssignmentResponse> responses = assignments.stream()
            .map(assignmentMapper::toResponse)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(
            ApiResponse.success("Assignments retrieved successfully", responses)
        );
    }
    
    /**
     * Get completed assignments for a specific crew
     * GET /api/assignments/crew/{crewId}/completed
     * 
     * @param crewId Crew ID
     * @return List of completed assignments for the crew
     */
    @GetMapping("/crew/{crewId}/completed")
    public ResponseEntity<ApiResponse<List<IncidentAssignmentResponse>>> getCompletedAssignments(
            @PathVariable Long crewId) {
        
        log.info("Fetching completed assignments for crew: {}", crewId);
        
        List<IncidentAssignment> assignments = assignmentService.getCompletedAssignments(crewId);
        List<IncidentAssignmentResponse> responses = assignments.stream()
            .map(assignmentMapper::toResponse)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(
            ApiResponse.success("Completed assignments retrieved successfully", responses)
        );
    }
    
    /**
     * Get assignments by user (assigned by)
     * GET /api/assignments/user/{userId}
     * 
     * @param userId User ID
     * @return List of assignments made by the user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<IncidentAssignmentResponse>>> getAssignmentsByUser(
            @PathVariable Long userId) {
        
        log.info("Fetching assignments made by user: {}", userId);
        
        List<IncidentAssignment> assignments = assignmentService.getAssignmentsByUser(userId);
        List<IncidentAssignmentResponse> responses = assignments.stream()
            .map(assignmentMapper::toResponse)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(
            ApiResponse.success("User assignments retrieved successfully", responses)
        );
    }
    
    /**
     * Get assignments within a date range
     * GET /api/assignments/date-range
     * 
     * @param startDate Start date (format: yyyy-MM-dd'T'HH:mm:ss)
     * @param endDate End date (format: yyyy-MM-dd'T'HH:mm:ss)
     * @return List of assignments in the date range
     */
    @GetMapping("/date-range")
    public ResponseEntity<ApiResponse<List<IncidentAssignmentResponse>>> getAssignmentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        log.info("Fetching assignments between {} and {}", startDate, endDate);
        
        List<IncidentAssignment> assignments = assignmentService.getAssignmentsByDateRange(startDate, endDate);
        List<IncidentAssignmentResponse> responses = assignments.stream()
            .map(assignmentMapper::toResponse)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(
            ApiResponse.success("Assignments in date range retrieved successfully", responses)
        );
    }
    
    /**
     * Start an assignment (change status to EN_CURSO)
     * PATCH /api/assignments/{id}/start
     * 
     * @param id Assignment ID
     * @return Updated assignment response
     */
    @PatchMapping("/{id}/start")
    public ResponseEntity<ApiResponse<IncidentAssignmentResponse>> startAssignment(@PathVariable Long id) {
        log.info("Starting assignment: {}", id);
        
        IncidentAssignment assignment = assignmentService.startAssignment(id);
        IncidentAssignmentResponse response = assignmentMapper.toResponse(assignment);
        
        return ResponseEntity.ok(
            ApiResponse.success("Assignment started successfully", response)
        );
    }
    
    /**
     * Complete an assignment
     * PATCH /api/assignments/{id}/complete
     * 
     * @param id Assignment ID
     * @param request CompleteAssignmentRequest with optional completion notes
     * @return Updated assignment response
     */
    @PatchMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<IncidentAssignmentResponse>> completeAssignment(
            @PathVariable Long id,
            @Valid @RequestBody(required = false) CompleteAssignmentRequest request) {
        
        log.info("Completing assignment: {}", id);
        
        IncidentAssignment assignment;
        if (request != null && request.getCompletionNotes() != null && !request.getCompletionNotes().isEmpty()) {
            assignment = assignmentService.completeAssignmentWithNotes(id, request.getCompletionNotes());
        } else {
            assignment = assignmentService.completeAssignment(id);
        }
        
        IncidentAssignmentResponse response = assignmentMapper.toResponse(assignment);
        
        return ResponseEntity.ok(
            ApiResponse.success("Assignment completed successfully", response)
        );
    }
    
    /**
     * Cancel an assignment
     * PATCH /api/assignments/{id}/cancel
     * 
     * @param id Assignment ID
     * @param request CancelAssignmentRequest with cancellation reason
     * @return Updated assignment response
     */
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<IncidentAssignmentResponse>> cancelAssignment(
            @PathVariable Long id,
            @Valid @RequestBody CancelAssignmentRequest request) {
        
        log.info("Cancelling assignment: {}", id);
        
        IncidentAssignment assignment = assignmentService.cancelAssignment(id, request.getReason());
        IncidentAssignmentResponse response = assignmentMapper.toResponse(assignment);
        
        return ResponseEntity.ok(
            ApiResponse.success("Assignment cancelled successfully", response)
        );
    }
    
    /**
     * Add notes to an assignment
     * POST /api/assignments/{id}/notes
     * 
     * @param id Assignment ID
     * @param request AddNotesRequest with notes to add
     * @return Updated assignment response
     */
    @PostMapping("/{id}/notes")
    public ResponseEntity<ApiResponse<IncidentAssignmentResponse>> addNotes(
            @PathVariable Long id,
            @Valid @RequestBody AddNotesRequest request) {
        
        log.info("Adding notes to assignment: {}", id);
        
        IncidentAssignment assignment = assignmentService.addNotes(id, request.getNotes());
        IncidentAssignmentResponse response = assignmentMapper.toResponse(assignment);
        
        return ResponseEntity.ok(
            ApiResponse.success("Notes added successfully", response)
        );
    }
    
    /**
     * Check if a crew has open assignments
     * GET /api/assignments/crew/{crewId}/has-open
     * 
     * @param crewId Crew ID
     * @return Boolean indicating if crew has open assignments
     */
    @GetMapping("/crew/{crewId}/has-open")
    public ResponseEntity<ApiResponse<Boolean>> hasOpenAssignments(@PathVariable Long crewId) {
        log.info("Checking if crew {} has open assignments", crewId);
        
        boolean hasOpen = assignmentService.hasOpenAssignments(crewId);
        
        return ResponseEntity.ok(
            ApiResponse.success("Open assignments status retrieved successfully", hasOpen)
        );
    }
    
    /**
     * Count active assignments for a crew
     * GET /api/assignments/crew/{crewId}/count/active
     * 
     * @param crewId Crew ID
     * @return Count of active assignments
     */
    @GetMapping("/crew/{crewId}/count/active")
    public ResponseEntity<ApiResponse<Long>> countActiveAssignments(@PathVariable Long crewId) {
        log.info("Counting active assignments for crew: {}", crewId);
        
        long count = assignmentService.countActiveAssignments(crewId);
        
        return ResponseEntity.ok(
            ApiResponse.success("Active assignments count retrieved successfully", count)
        );
    }
    
    /**
     * Count completed assignments for a crew
     * GET /api/assignments/crew/{crewId}/count/completed
     * 
     * @param crewId Crew ID
     * @return Count of completed assignments
     */
    @GetMapping("/crew/{crewId}/count/completed")
    public ResponseEntity<ApiResponse<Long>> countCompletedAssignments(@PathVariable Long crewId) {
        log.info("Counting completed assignments for crew: {}", crewId);
        
        long count = assignmentService.countCompletedAssignments(crewId);
        
        return ResponseEntity.ok(
            ApiResponse.success("Completed assignments count retrieved successfully", count)
        );
    }
}
