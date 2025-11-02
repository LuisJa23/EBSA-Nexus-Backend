package co.com.ebsa.ebsa_nexus.presentation.controller;

import co.com.ebsa.ebsa_nexus.application.dto.request.auth.ChangePasswordRequest;
import co.com.ebsa.ebsa_nexus.application.dto.request.auth.CreateUserRequest;
import co.com.ebsa.ebsa_nexus.application.dto.request.auth.UpdateOwnProfileRequest;
import co.com.ebsa.ebsa_nexus.application.dto.request.auth.UpdateUserRequest;
import co.com.ebsa.ebsa_nexus.application.dto.response.UserResponse;
import co.com.ebsa.ebsa_nexus.application.service.UserManagementService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserManagementController {
    
    private final UserManagementService userManagementService;
    
    public UserManagementController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request,
            Authentication authentication) {
        
        log.info("Creating user request received for email: {} by admin: {}", 
                request.email(), authentication.getName());
                
        UserResponse response = userManagementService.createUser(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRequest request,
            Authentication authentication) {
        
        log.info("Updating user request received for ID: {} by admin: {}", 
                userId, authentication.getName());
                
        UserResponse response = userManagementService.updateUser(userId, request, authentication.getName());
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{userId}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateUser(
            @PathVariable Long userId,
            Authentication authentication) {
        
        log.info("Deactivating user request received for ID: {} by admin: {}", 
                userId, authentication.getName());
                
        userManagementService.deactivateUser(userId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{userId}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activateUser(
            @PathVariable Long userId,
            Authentication authentication) {
        
        log.info("Activating user request received for ID: {} by admin: {}", 
                userId, authentication.getName());
                
        userManagementService.activateUser(userId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long userId,
            Authentication authentication) {
        
        log.debug("Get user by ID request received for ID: {} by admin: {}", 
                userId, authentication.getName());
                
        UserResponse response = userManagementService.getUserById(userId, authentication.getName());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable,
            Authentication authentication) {
        
        log.debug("Get all users request received by admin: {} with pageable: {}", 
                authentication.getName(), pageable);
                
        Page<UserResponse> response = userManagementService.getAllUsers(pageable, authentication.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/available-for-crew")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponse>> getAvailableUsers(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable,
            Authentication authentication) {
        
        log.debug("Get available users request received by admin: {} with pageable: {}", 
                authentication.getName(), pageable);
                
        Page<UserResponse> response = userManagementService.getAvailableUsers(pageable, authentication.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        log.info("User requesting own profile data: {}", authentication.getName());
        
        UserResponse response = userManagementService.getCurrentUser(authentication.getName());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/me")
    public ResponseEntity<UserResponse> updateOwnProfile(
            @Valid @RequestBody UpdateOwnProfileRequest request,
            Authentication authentication) {
        
        log.info("User updating own profile: {}", authentication.getName());
        
        UserResponse response = userManagementService.updateOwnProfile(request, authentication.getName());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint para cambiar la contraseña del usuario autenticado.
     * El usuario debe estar autenticado (cualquier rol).
     * Se requiere la contraseña actual para validación de seguridad.
     * 
     * @param request contiene contraseña actual, nueva y confirmación
     * @param authentication contiene el email del usuario autenticado (extraído del JWT)
     * @return 204 No Content si el cambio fue exitoso
     */
        @PatchMapping("/me/change-password")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {
        
        log.info("User requesting password change: {}", authentication.getName());
        
        userManagementService.changePassword(request, authentication.getName());
        
        return ResponseEntity.noContent().build();
    }
}