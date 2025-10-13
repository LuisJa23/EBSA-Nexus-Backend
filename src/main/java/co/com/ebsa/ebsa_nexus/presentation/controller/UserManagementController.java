package co.com.ebsa.ebsa_nexus.presentation.controller;

import co.com.ebsa.ebsa_nexus.application.dto.request.CreateUserRequest;
import co.com.ebsa.ebsa_nexus.application.dto.request.UpdateUserRequest;
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

/**
 * Controller REST para la gestión de usuarios del sistema.
 * Todos los endpoints requieren autenticación y rol de ADMIN.
 * 
 * Funcionalidades:
 * - Crear nuevos usuarios (no admin)
 * - Actualizar usuarios existentes  
 * - Desactivar usuarios
 * - Consultar usuarios por ID
 * - Listar usuarios con paginación
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserManagementController {
    
    private final UserManagementService userManagementService;
    
    public UserManagementController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }
    
    /**
     * Crea un nuevo usuario en el sistema.
     * Solo usuarios con rol ADMIN pueden crear usuarios.
     * No se pueden crear usuarios con rol ADMIN.
     */
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
    
    /**
     * Actualiza los datos de un usuario existente.
     * Solo usuarios con rol ADMIN pueden actualizar usuarios.
     * Un admin no puede desactivarse a sí mismo.
     */
    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Integer userId,
            @Valid @RequestBody UpdateUserRequest request,
            Authentication authentication) {
        
        log.info("Updating user request received for ID: {} by admin: {}", 
                userId, authentication.getName());
                
        UserResponse response = userManagementService.updateUser(userId, request, authentication.getName());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Desactiva un usuario del sistema.
     * Solo usuarios con rol ADMIN pueden desactivar usuarios.
     * Un admin no puede desactivarse a sí mismo.
     */
    @PatchMapping("/{userId}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateUser(
            @PathVariable Integer userId,
            Authentication authentication) {
        
        log.info("Deactivating user request received for ID: {} by admin: {}", 
                userId, authentication.getName());
                
        userManagementService.deactivateUser(userId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Obtiene los detalles de un usuario específico.
     * Solo usuarios con rol ADMIN pueden consultar detalles de usuarios.
     */
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Integer userId,
            Authentication authentication) {
        
        log.debug("Get user by ID request received for ID: {} by admin: {}", 
                userId, authentication.getName());
                
        UserResponse response = userManagementService.getUserById(userId, authentication.getName());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Obtiene una lista paginada de todos los usuarios del sistema.
     * Solo usuarios con rol ADMIN pueden listar usuarios.
     */
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
}