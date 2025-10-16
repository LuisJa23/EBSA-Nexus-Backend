package co.com.ebsa.ebsa_nexus.presentation.controllers;

import co.com.ebsa.ebsa_nexus.application.crew.services.CrewMemberService;
import co.com.ebsa.ebsa_nexus.domain.crew.entities.CrewMember;
import co.com.ebsa.ebsa_nexus.presentation.dto.request.AddMemberRequest;
import co.com.ebsa.ebsa_nexus.presentation.dto.response.ApiResponse;
import co.com.ebsa.ebsa_nexus.presentation.dto.response.CrewMemberResponse;
import co.com.ebsa.ebsa_nexus.presentation.mappers.CrewMemberDtoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for crew member management operations
 * Provides endpoints for managing crew memberships and leadership
 * 
 * Base path: /api/crews/{crewId}/members
 */
@Slf4j
@RestController
@RequestMapping("/api/crews")
@RequiredArgsConstructor
public class CrewMemberController {
    
    private final CrewMemberService memberService;
    private final CrewMemberDtoMapper memberMapper;
    
    /**
     * Add a member to a crew
     * POST /api/crews/{crewId}/members
     * 
     * @param crewId Crew ID
     * @param request AddMemberRequest with user information
     * @return Created member response with HTTP 201
     */
    @PostMapping("/{crewId}/members")
    public ResponseEntity<ApiResponse<CrewMemberResponse>> addMember(
            @PathVariable Long crewId,
            @Valid @RequestBody AddMemberRequest request) {
        
        log.info("Adding member {} to crew {}", request.getUserId(), crewId);
        
        CrewMember member;
        if (Boolean.TRUE.equals(request.getIsLeader())) {
            member = memberService.addLeader(crewId, request.getUserId());
            log.info("Member added as leader");
        } else {
            member = memberService.addMember(crewId, request.getUserId());
            log.info("Member added as regular member");
        }
        
        CrewMemberResponse response = memberMapper.toResponse(member);
        
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("Member added successfully", response));
    }
    
    /**
     * Get all active members of a crew
     * GET /api/crews/{crewId}/members
     * 
     * @param crewId Crew ID
     * @return List of active members
     */
    @GetMapping("/{crewId}/members")
    public ResponseEntity<ApiResponse<List<CrewMemberResponse>>> getCrewMembers(@PathVariable Long crewId) {
        log.info("Fetching members of crew: {}", crewId);
        
        List<CrewMember> members = memberService.getActiveMembers(crewId);
        List<CrewMemberResponse> responses = members.stream()
            .map(memberMapper::toResponse)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(
            ApiResponse.success("Members retrieved successfully", responses)
        );
    }
    
    /**
     * Get all members of a crew (including inactive)
     * GET /api/crews/{crewId}/members/all
     * 
     * @param crewId Crew ID
     * @return List of all members
     */
    @GetMapping("/{crewId}/members/all")
    public ResponseEntity<ApiResponse<List<CrewMemberResponse>>> getAllCrewMembers(@PathVariable Long crewId) {
        log.info("Fetching all members (including inactive) of crew: {}", crewId);
        
        List<CrewMember> members = memberService.getAllMembers(crewId);
        List<CrewMemberResponse> responses = members.stream()
            .map(memberMapper::toResponse)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(
            ApiResponse.success("All members retrieved successfully", responses)
        );
    }
    
    /**
     * Get the leader of a crew
     * GET /api/crews/{crewId}/leader
     * 
     * @param crewId Crew ID
     * @return Leader information
     */
    @GetMapping("/{crewId}/leader")
    public ResponseEntity<ApiResponse<CrewMemberResponse>> getCrewLeader(@PathVariable Long crewId) {
        log.info("Fetching leader of crew: {}", crewId);
        
        CrewMember leader = memberService.getLeader(crewId);
        CrewMemberResponse response = memberMapper.toResponse(leader);
        
        return ResponseEntity.ok(
            ApiResponse.success("Leader retrieved successfully", response)
        );
    }
    
    /**
     * Remove a member from a crew
     * DELETE /api/crews/{crewId}/members/{memberId}
     * 
     * @param crewId Crew ID
     * @param memberId Member ID
     * @return Success response
     */
    @DeleteMapping("/{crewId}/members/{memberId}")
    public ResponseEntity<ApiResponse<Void>> removeMember(
            @PathVariable Long crewId,
            @PathVariable Long memberId) {
        
        log.info("Removing member {} from crew {}", memberId, crewId);
        
        memberService.removeMember(crewId, memberId);
        
        return ResponseEntity.ok(
            ApiResponse.success("Member removed successfully")
        );
    }
    
    /**
     * Promote a member to leader
     * PATCH /api/crews/{crewId}/members/{memberId}/promote
     * 
     * @param crewId Crew ID
     * @param memberId Member ID
     * @return Updated member response
     */
    @PatchMapping("/{crewId}/members/{memberId}/promote")
    public ResponseEntity<ApiResponse<CrewMemberResponse>> promoteToLeader(
            @PathVariable Long crewId,
            @PathVariable Long memberId) {
        
        log.info("Promoting member {} to leader in crew {}", memberId, crewId);
        
        CrewMember member = memberService.promoteToLeader(crewId, memberId);
        CrewMemberResponse response = memberMapper.toResponse(member);
        
        return ResponseEntity.ok(
            ApiResponse.success("Member promoted to leader successfully", response)
        );
    }
    
    /**
     * Demote a leader to regular member
     * PATCH /api/crews/{crewId}/members/{memberId}/demote
     * 
     * @param crewId Crew ID
     * @param memberId Member ID (must be current leader)
     * @return Updated member response
     */
    @PatchMapping("/{crewId}/members/{memberId}/demote")
    public ResponseEntity<ApiResponse<CrewMemberResponse>> demoteFromLeader(
            @PathVariable Long crewId,
            @PathVariable Long memberId) {
        
        log.info("Demoting leader {} to regular member in crew {}", memberId, crewId);
        
        CrewMember member = memberService.demoteFromLeader(crewId, memberId);
        CrewMemberResponse response = memberMapper.toResponse(member);
        
        return ResponseEntity.ok(
            ApiResponse.success("Leader demoted to regular member successfully", response)
        );
    }
    
    /**
     * Get crew membership history for a user
     * GET /api/users/{userId}/crew-history
     * 
     * @param userId User ID
     * @return List of all crew memberships (current and past)
     */
    @GetMapping("/users/{userId}/crew-history")
    public ResponseEntity<ApiResponse<List<CrewMemberResponse>>> getUserCrewHistory(@PathVariable Long userId) {
        log.info("Fetching crew history for user: {}", userId);
        
        List<CrewMember> history = memberService.getUserHistory(userId);
        List<CrewMemberResponse> responses = history.stream()
            .map(memberMapper::toResponse)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(
            ApiResponse.success("User crew history retrieved successfully", responses)
        );
    }
    
    /**
     * Get all active leaders across all crews
     * GET /api/crews/leaders
     * 
     * @return List of all active leaders
     */
    @GetMapping("/leaders")
    public ResponseEntity<ApiResponse<List<CrewMemberResponse>>> getAllLeaders() {
        log.info("Fetching all active leaders");
        
        List<CrewMember> leaders = memberService.getAllActiveLeaders();
        List<CrewMemberResponse> responses = leaders.stream()
            .map(memberMapper::toResponse)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(
            ApiResponse.success("Leaders retrieved successfully", responses)
        );
    }
    
    /**
     * Count active members in a crew
     * GET /api/crews/{crewId}/members/count
     * 
     * @param crewId Crew ID
     * @return Count of active members
     */
    @GetMapping("/{crewId}/members/count")
    public ResponseEntity<ApiResponse<Long>> countActiveMembers(@PathVariable Long crewId) {
        log.info("Counting active members in crew: {}", crewId);
        
        long count = memberService.countActiveMembers(crewId);
        
        return ResponseEntity.ok(
            ApiResponse.success("Member count retrieved successfully", count)
        );
    }
    
    /**
     * Check if a user is currently in an active crew
     * GET /api/users/{userId}/is-in-crew
     * 
     * @param userId User ID
     * @return Boolean indicating if user is in an active crew
     */
    @GetMapping("/users/{userId}/is-in-crew")
    public ResponseEntity<ApiResponse<Boolean>> isUserInActiveCrew(@PathVariable Long userId) {
        log.info("Checking if user {} is in an active crew", userId);
        
        boolean isInCrew = memberService.isUserInActiveCrew(userId);
        
        return ResponseEntity.ok(
            ApiResponse.success("User crew status retrieved successfully", isInCrew)
        );
    }
}
