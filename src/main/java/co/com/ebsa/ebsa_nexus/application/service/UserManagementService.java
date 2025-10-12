package co.com.ebsa.ebsa_nexus.application.service;

import co.com.ebsa.ebsa_nexus.application.dto.request.CreateUserRequest;
import co.com.ebsa.ebsa_nexus.application.dto.request.UpdateUserRequest;
import co.com.ebsa.ebsa_nexus.application.dto.response.UserResponse;
import co.com.ebsa.ebsa_nexus.domain.entity.Role;
import co.com.ebsa.ebsa_nexus.domain.entity.User;
import co.com.ebsa.ebsa_nexus.domain.entity.WorkRole;
import co.com.ebsa.ebsa_nexus.domain.exception.*;
import co.com.ebsa.ebsa_nexus.domain.repository.UserDomainRepository;
import co.com.ebsa.ebsa_nexus.infrastructure.repository.RoleRepository;
import co.com.ebsa.ebsa_nexus.infrastructure.repository.WorkRoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Servicio de aplicación para la gestión de usuarios.
 * Implementa toda la lógica de negocio relacionada con CRUD de usuarios,
 * validaciones de seguridad y reglas de negocio específicas.
 */
@Slf4j
@Service
@Transactional
public class UserManagementService {
    
    private final UserDomainRepository userRepository;
    private final RoleRepository roleRepository;
    private final WorkRoleRepository workRoleRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UserManagementService(UserDomainRepository userRepository,
                                RoleRepository roleRepository,
                                WorkRoleRepository workRoleRepository,
                                PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.workRoleRepository = workRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * Crea un nuevo usuario en el sistema.
     * Solo usuarios con rol ADMIN pueden crear usuarios.
     * No se pueden crear usuarios con rol ADMIN.
     */
    public UserResponse createUser(CreateUserRequest request, String currentUserEmail) {
        log.info("Creating user with email: {} by admin: {}", request.email(), currentUserEmail);
        
        // Validar que el usuario actual es admin
        validateAdminRole(currentUserEmail);
        
        // Validar que no se está creando un admin
        validateNotCreatingAdmin(request.roleId());
        
        // Validar unicidad
        validateUserUniqueness(request.email(), request.username());
        
        // Validar que el rol existe
        Role role = roleRepository.findById(request.roleId())
            .orElseThrow(() -> new UserNotFoundException("Rol con ID " + request.roleId() + " no encontrado"));
        
        // Validar work role si se proporciona
        WorkRole workRole = null;
        if (request.workRoleId() != null) {
            workRole = workRoleRepository.findById(request.workRoleId())
                .orElseThrow(() -> new UserNotFoundException("Work Role con ID " + request.workRoleId() + " no encontrado"));
        }
        
        // Crear usuario
        User user = User.builder()
            .uuid(UUID.randomUUID().toString())
            .username(request.username())
            .email(request.email())
            .pwdHash(passwordEncoder.encode(request.password()))
            .firstName(request.firstName())
            .lastName(request.lastName())
            .roleId(request.roleId())
            .workRoleId(request.workRoleId())
            .workType(request.workType())
            .documentNumber(request.documentNumber())
            .phone(request.phone())
            .active(true)
            .build();
        
        User savedUser = userRepository.save(user);
        
        log.info("User created successfully with ID: {}", savedUser.getId());
        return mapToUserResponse(savedUser, role, workRole);
    }
    
    /**
     * Actualiza un usuario existente.
     * Solo usuarios con rol ADMIN pueden actualizar usuarios.
     * Un admin no puede desactivarse a sí mismo.
     */
    public UserResponse updateUser(Integer userId, UpdateUserRequest request, String currentUserEmail) {
        log.info("Updating user ID: {} by admin: {}", userId, currentUserEmail);
        
        // Validar que el usuario actual es admin
        validateAdminRole(currentUserEmail);
        
        // Buscar usuario a actualizar
        User existingUser = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        // Prevenir que admin se desactive a sí mismo
        if (currentUserEmail.equals(existingUser.getEmail()) && 
            request.active() != null && !request.active()) {
            throw new UnauthorizedOperationException("No puedes desactivar tu propia cuenta");
        }
        
        // Validar unicidad si se cambian email o username
        validateUpdateUniqueness(existingUser, request.email(), request.username());
        
        // Actualizar campos no nulos
        if (request.username() != null) existingUser.setUsername(request.username());
        if (request.email() != null) existingUser.setEmail(request.email());
        if (request.password() != null) existingUser.setPwdHash(passwordEncoder.encode(request.password()));
        if (request.firstName() != null) existingUser.setFirstName(request.firstName());
        if (request.lastName() != null) existingUser.setLastName(request.lastName());
        if (request.roleId() != null) {
            // Validar que no se está convirtiendo en admin
            validateNotCreatingAdmin(request.roleId());
            existingUser.setRoleId(request.roleId());
        }
        if (request.workRoleId() != null) existingUser.setWorkRoleId(request.workRoleId());
        if (request.workType() != null) existingUser.setWorkType(request.workType());
        if (request.documentNumber() != null) existingUser.setDocumentNumber(request.documentNumber());
        if (request.phone() != null) existingUser.setPhone(request.phone());
        if (request.active() != null) existingUser.setActive(request.active());
        
        User updatedUser = userRepository.save(existingUser);
        
        // Cargar relaciones para la respuesta
        Role role = roleRepository.findById(updatedUser.getRoleId()).orElse(null);
        WorkRole workRole = updatedUser.getWorkRoleId() != null ? 
            workRoleRepository.findById(updatedUser.getWorkRoleId()).orElse(null) : null;
        
        log.info("User updated successfully with ID: {}", updatedUser.getId());
        return mapToUserResponse(updatedUser, role, workRole);
    }
    
    /**
     * Desactiva un usuario del sistema.
     * Solo usuarios con rol ADMIN pueden desactivar usuarios.
     * Un admin no puede desactivarse a sí mismo.
     */
    public void deactivateUser(Integer userId, String currentUserEmail) {
        log.info("Deactivating user ID: {} by admin: {}", userId, currentUserEmail);
        
        // Validar que el usuario actual es admin
        validateAdminRole(currentUserEmail);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        // Prevenir que admin se desactive a sí mismo
        if (currentUserEmail.equals(user.getEmail())) {
            throw new UnauthorizedOperationException("No puedes desactivar tu propia cuenta");
        }
        
        user.setActive(false);
        userRepository.save(user);
        
        log.info("User deactivated successfully with ID: {}", userId);
    }
    
    /**
     * Obtiene un usuario por su ID.
     * Solo usuarios con rol ADMIN pueden ver detalles de usuarios.
     */
    @Transactional(readOnly = true)
    public UserResponse getUserById(Integer userId, String currentUserEmail) {
        validateAdminRole(currentUserEmail);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        Role role = roleRepository.findById(user.getRoleId()).orElse(null);
        WorkRole workRole = user.getWorkRoleId() != null ? 
            workRoleRepository.findById(user.getWorkRoleId()).orElse(null) : null;
        
        return mapToUserResponse(user, role, workRole);
    }
    
    /**
     * Obtiene todos los usuarios con paginación.
     * Solo usuarios con rol ADMIN pueden listar usuarios.
     */
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable, String currentUserEmail) {
        validateAdminRole(currentUserEmail);
        
        return userRepository.findAll(pageable)
            .map(user -> {
                Role role = roleRepository.findById(user.getRoleId()).orElse(null);
                WorkRole workRole = user.getWorkRoleId() != null ? 
                    workRoleRepository.findById(user.getWorkRoleId()).orElse(null) : null;
                return mapToUserResponse(user, role, workRole);
            });
    }
    
    /**
     * Valida que el usuario actual tiene rol de ADMIN.
     */
    private void validateAdminRole(String currentUserEmail) {
        User currentUser = userRepository.findByEmail(currentUserEmail)
            .orElseThrow(() -> new UnauthorizedOperationException("Usuario no encontrado"));
        
        Role role = roleRepository.findById(currentUser.getRoleId()).orElse(null);
        if (role == null || !"ADMIN".equals(role.getName())) {
            throw new UnauthorizedOperationException("Solo los administradores pueden gestionar usuarios");
        }
    }
    
    /**
     * Valida que no se esté intentando crear un usuario con rol ADMIN.
     */
    private void validateNotCreatingAdmin(Integer roleId) {
        Role role = roleRepository.findById(roleId).orElse(null);
        if (role != null && "ADMIN".equals(role.getName())) {
            throw new UnauthorizedOperationException("No se pueden crear usuarios administradores");
        }
    }
    
    /**
     * Valida que email y username sean únicos para nuevos usuarios.
     */
    private void validateUserUniqueness(String email, String username) {
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("Ya existe un usuario con el email: " + email);
        }
        if (userRepository.existsByUsername(username)) {
            throw new UserAlreadyExistsException("Ya existe un usuario con el username: " + username);
        }
    }
    
    /**
     * Valida unicidad para actualizaciones de usuarios existentes.
     */
    private void validateUpdateUniqueness(User existingUser, String newEmail, String newUsername) {
        if (newEmail != null && !newEmail.equals(existingUser.getEmail()) && 
            userRepository.existsByEmail(newEmail)) {
            throw new UserAlreadyExistsException("Ya existe un usuario con el email: " + newEmail);
        }
        if (newUsername != null && !newUsername.equals(existingUser.getUsername()) && 
            userRepository.existsByUsername(newUsername)) {
            throw new UserAlreadyExistsException("Ya existe un usuario con el username: " + newUsername);
        }
    }
    
    /**
     * Convierte una entidad User a UserResponse incluyendo nombres de roles.
     */
    private UserResponse mapToUserResponse(User user, Role role, WorkRole workRole) {
        return new UserResponse(
            user.getId(),
            user.getUuid(),
            user.getUsername(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            role != null ? role.getName() : null,
            workRole != null ? workRole.getName() : null,
            user.getWorkType(),
            user.getDocumentNumber(),
            user.getPhone(),
            user.getActive(),
            user.getCreatedAt(),
            user.getUpdatedAt(),
            user.getLastLogin()
        );
    }
}