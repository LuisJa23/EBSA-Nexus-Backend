package co.com.ebsa.ebsa_nexus.presentation.controller;

import co.com.ebsa.ebsa_nexus.application.dto.request.crew.ChangeCrewStatusRequest;
import co.com.ebsa.ebsa_nexus.application.dto.request.crew.CreateCrewRequest;
import co.com.ebsa.ebsa_nexus.application.dto.request.crew.CreateCrewMemberRequest;
import co.com.ebsa.ebsa_nexus.application.dto.request.crew.UpdateCrewRequest;
import co.com.ebsa.ebsa_nexus.application.dto.response.ApiResponse;
import co.com.ebsa.ebsa_nexus.application.dto.response.CrewDetailResponse;
import co.com.ebsa.ebsa_nexus.application.dto.response.CrewMemberResponse;
import co.com.ebsa.ebsa_nexus.application.dto.response.CrewResponse;
import co.com.ebsa.ebsa_nexus.application.service.CrewMemberService;
import co.com.ebsa.ebsa_nexus.application.service.CrewService;
import co.com.ebsa.ebsa_nexus.application.service.IncidentAssignmentService;
import co.com.ebsa.ebsa_nexus.domain.repository.UserDomainRepository;
import co.com.ebsa.ebsa_nexus.domain.enums.CrewStatus;
import co.com.ebsa.ebsa_nexus.domain.entity.Crew;
import co.com.ebsa.ebsa_nexus.domain.entity.CrewMember;
import co.com.ebsa.ebsa_nexus.domain.entity.User;
import co.com.ebsa.ebsa_nexus.presentation.mapper.CrewDtoMapper;
import co.com.ebsa.ebsa_nexus.presentation.mapper.CrewMemberDtoMapper;
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
    private final CrewMemberService memberService;
    private final IncidentAssignmentService assignmentService;
    private final UserDomainRepository userRepository;
    private final CrewDtoMapper crewMapper;
    private final CrewMemberDtoMapper memberMapper;
    
    /**
     * Create a new crew
     * POST /api/crews
     * 
     * @param request CreateCrewRequest with crew information
     * @return Created crew response with HTTP 201
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CrewResponse>> createCrew(@Valid @RequestBody CreateCrewRequest request) {
        log.info("Creating new crew: {} with {} members", request.getName(), request.getMembers().size());
        
        // Validar que hay al menos un miembro
        if (request.getMembers() == null || request.getMembers().isEmpty()) {
            throw new IllegalArgumentException("No se puede crear una cuadrilla vacía. Debe tener al menos un miembro.");
        }
        
        // Validar que hay exactamente un líder
        long leaderCount = request.getMembers().stream()
                .filter(member -> Boolean.TRUE.equals(member.getIsLeader()))
                .count();
        
        if (leaderCount == 0) {
            throw new IllegalArgumentException("La cuadrilla debe tener exactamente un líder.");
        }
        
        if (leaderCount > 1) {
            throw new IllegalArgumentException("La cuadrilla no puede tener más de un líder.");
        }
        
        // Crear la cuadrilla
        Crew crew = crewService.createCrew(
            request.getName(),
            request.getDescription(),
            request.getCreatedBy()
        );
        
        // Asignar miembros a la cuadrilla
        for (CreateCrewMemberRequest memberRequest : request.getMembers()) {
            if (Boolean.TRUE.equals(memberRequest.getIsLeader())) {
                // Agregar como líder
                memberService.addLeader(crew.getId(), memberRequest.getUserId());
                log.info("Added leader {} to crew {}", memberRequest.getUserId(), crew.getId());
            } else {
                // Agregar como miembro regular
                memberService.addMember(crew.getId(), memberRequest.getUserId());
                log.info("Added member {} to crew {}", memberRequest.getUserId(), crew.getId());
            }
        }
        
        CrewResponse response = crewMapper.toResponse(crew);
        
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("Crew created successfully with members", response));
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
     * @return Crew information including members
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CrewDetailResponse>> getCrewById(@PathVariable Long id) {
        log.info("Fetching crew with ID: {}", id);
        
        // Obtener la cuadrilla
        Crew crew = crewService.getCrewById(id);
        
        // Obtener miembros activos
        List<CrewMember> members = memberService.getActiveMembers(id);
        
        // Convertir miembros a response con información del usuario
        List<CrewMemberResponse> memberResponses = members.stream()
            .map(member -> {
                // Obtener información del usuario
                User user = userRepository.findById(member.getUserId()).orElse(null);
                String username = user != null ? user.getUsername() : "Usuario-" + member.getUserId();
                String fullName = user != null ? user.getFirstName() + " " + user.getLastName() : "Usuario Desconocido";
                
                // Usar el mapper completo
                return memberMapper.toResponse(member, crew.getName(), username, fullName);
            })
            .collect(Collectors.toList());
        
        // Obtener información del líder
        Long leaderId = null;
        String leaderUsername = null;
        try {
            CrewMember leader = memberService.getLeader(id);
            leaderId = leader.getUserId();
            User leaderUser = userRepository.findById(leaderId).orElse(null);
            leaderUsername = leaderUser != null ? leaderUser.getUsername() : "Leader-" + leaderId;
        } catch (Exception e) {
            log.warn("No leader found for crew {}", id);
        }
        
        // Get additional statistics
        int memberCount = memberResponses.size();
        boolean hasAssignments = assignmentService.hasOpenAssignments(id);
        
        CrewDetailResponse response = crewMapper.toDetailResponse(crew, memberResponses, leaderId, leaderUsername, memberCount, hasAssignments);
        
        return ResponseEntity.ok(
            ApiResponse.success("Crew retrieved successfully", response)
        );
    }

    /**
     * Get crew by ID with detailed information including members and leader
     * GET /api/crews/{id}/details
     * 
     * @param id Crew ID
     * @return Detailed crew information including members list with leader indicator
     */
    @GetMapping("/{id}/details")
    public ResponseEntity<ApiResponse<CrewDetailResponse>> getCrewDetails(@PathVariable Long id) {
        log.info("Fetching detailed crew information with ID: {}", id);
        
        // Obtener la cuadrilla
        Crew crew = crewService.getCrewById(id);
        
        // Obtener miembros activos
        List<CrewMember> members = memberService.getActiveMembers(id);
        
        // Convertir miembros a response con información del usuario
        List<CrewMemberResponse> memberResponses = members.stream()
            .map(member -> {
                // Obtener información del usuario
                User user = userRepository.findById(member.getUserId()).orElse(null);
                String username = user != null ? user.getUsername() : "Usuario-" + member.getUserId();
                String fullName = user != null ? user.getFirstName() + " " + user.getLastName() : "Usuario Desconocido";
                
                // Usar el mapper completo
                return memberMapper.toResponse(member, crew.getName(), username, fullName);
            })
            .collect(Collectors.toList());
        
        // Obtener información del líder
        Long leaderId = null;
        String leaderUsername = null;
        try {
            CrewMember leader = memberService.getLeader(id);
            leaderId = leader.getUserId();
            User leaderUser = userRepository.findById(leaderId).orElse(null);
            leaderUsername = leaderUser != null ? leaderUser.getUsername() : "Leader-" + leaderId;
        } catch (Exception e) {
            log.warn("No leader found for crew {}", id);
        }
        
        // Get additional statistics
        int memberCount = memberResponses.size();
        boolean hasAssignments = assignmentService.hasOpenAssignments(id);
        
        // Crear respuesta detallada
        CrewDetailResponse response = crewMapper.toDetailResponse(crew, memberResponses, leaderId, leaderUsername, memberCount, hasAssignments);
        
        return ResponseEntity.ok(
            ApiResponse.success("Detailed crew information retrieved successfully", response)
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
