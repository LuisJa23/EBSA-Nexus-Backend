package co.com.ebsa.ebsa_nexus.application.service;

import co.com.ebsa.ebsa_nexus.application.dto.request.CreateUserRequest;
import co.com.ebsa.ebsa_nexus.application.dto.request.UpdateOwnProfileRequest;
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

import java.util.HashMap;
import java.util.Map;
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
        validateNotCreatingAdmin(request.roleName());
        
        // Validar unicidad (email, username, documento, teléfono)
        validateUserUniqueness(request.email(), request.username(), 
                              request.documentNumber(), request.phone());
        
        // Buscar el rol por nombre
        Role role = roleRepository.findByName(request.roleName())
            .orElseThrow(() -> new UserNotFoundException("Rol '" + request.roleName() + "' no encontrado"));
        
        // Convertir workType string a enum
        User.WorkType workTypeEnum = User.WorkType.valueOf(request.workType());
        
        // Validar work role si se proporciona
        WorkRole workRole = null;
        if (request.workRoleName() != null && !request.workRoleName().isBlank()) {
            workRole = workRoleRepository.findByName(request.workRoleName())
                .orElseThrow(() -> new UserNotFoundException("Work Role '" + request.workRoleName() + "' no encontrado"));
        }
        
        // Validar que el WorkRole coincida con el WorkType
        validateWorkRoleMatchesWorkType(workRole != null ? workRole.getId() : null, workTypeEnum);
        
        // Crear usuario
        User user = User.builder()
            .uuid(UUID.randomUUID().toString())
            .username(request.username())
            .email(request.email())
            .pwdHash(passwordEncoder.encode(request.password()))
            .firstName(request.firstName())
            .lastName(request.lastName())
            .roleId(role.getId())
            .workRoleId(workRole != null ? workRole.getId() : null)
            .workType(workTypeEnum)
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
        validateUpdateUniqueness(userId, request.email(), request.username(), 
                                request.documentNumber(), request.phone());
        
        // Actualizar campos no nulos
        if (request.username() != null) existingUser.setUsername(request.username());
        if (request.email() != null) existingUser.setEmail(request.email());
        if (request.password() != null) existingUser.setPwdHash(passwordEncoder.encode(request.password()));
        if (request.firstName() != null) existingUser.setFirstName(request.firstName());
        if (request.lastName() != null) existingUser.setLastName(request.lastName());
        if (request.roleId() != null) {
            // Validar que el rol existe
            Role newRole = roleRepository.findById(request.roleId())
                .orElseThrow(() -> new UserNotFoundException("Rol con ID " + request.roleId() + " no encontrado"));
            // Validar que no se está convirtiendo en admin
            validateNotCreatingAdmin(newRole.getName());
            existingUser.setRoleId(request.roleId());
        }
        if (request.workRoleId() != null) existingUser.setWorkRoleId(request.workRoleId());
        if (request.workType() != null) existingUser.setWorkType(request.workType());
        if (request.documentNumber() != null) existingUser.setDocumentNumber(request.documentNumber());
        if (request.phone() != null) existingUser.setPhone(request.phone());
        if (request.active() != null) existingUser.setActive(request.active());
        
        // Validar WorkRole si se actualiza workRoleId o workType
        User.WorkType finalWorkType = request.workType() != null ? 
            request.workType() : existingUser.getWorkType();
        Integer finalWorkRoleId = request.workRoleId() != null ? 
            request.workRoleId() : existingUser.getWorkRoleId();
        validateWorkRoleMatchesWorkType(finalWorkRoleId, finalWorkType);
        
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
     * Obtiene los datos del usuario autenticado actual.
     * Extrae el email del token JWT y retorna todos los datos del usuario.
     */
    public UserResponse getCurrentUser(String authenticatedUserEmail) {
        log.info("Getting current user data for: {}", authenticatedUserEmail);
        
        // Buscar el usuario autenticado
        User user = userRepository.findByEmail(authenticatedUserEmail)
            .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        
        // Cargar relaciones para la respuesta
        Role role = roleRepository.findById(user.getRoleId()).orElse(null);
        WorkRole workRole = user.getWorkRoleId() != null ? 
            workRoleRepository.findById(user.getWorkRoleId()).orElse(null) : null;
        
        log.info("Current user data retrieved successfully: {}", authenticatedUserEmail);
        return mapToUserResponse(user, role, workRole);
    }

    /**
     * Permite a un usuario actualizar su propio perfil.
     * Solo puede actualizar: firstName, lastName y phone.
     * No puede modificar email, username, password, documentNumber, roles ni permisos.
     * Se valida mediante el email del token JWT que el usuario solo actualice sus propios datos.
     */
    public UserResponse updateOwnProfile(UpdateOwnProfileRequest request, String authenticatedUserEmail) {
        log.info("User updating own profile: {}", authenticatedUserEmail);
        
        // Buscar el usuario autenticado
        User user = userRepository.findByEmail(authenticatedUserEmail)
            .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        
        // Actualizar solo los campos permitidos
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPhone(request.phone());
        
        User updatedUser = userRepository.save(user);
        
        // Cargar relaciones para la respuesta
        Role role = roleRepository.findById(updatedUser.getRoleId()).orElse(null);
        WorkRole workRole = updatedUser.getWorkRoleId() != null ? 
            workRoleRepository.findById(updatedUser.getWorkRoleId()).orElse(null) : null;
        
        log.info("User profile updated successfully: {}", authenticatedUserEmail);
        return mapToUserResponse(updatedUser, role, workRole);
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
    private void validateNotCreatingAdmin(String roleName) {
        if ("ADMIN".equalsIgnoreCase(roleName)) {
            throw new UnauthorizedOperationException("No se pueden crear usuarios administradores");
        }
    }
    
    /**
     * Valida que email y username sean únicos para nuevos usuarios.
     * Acumula TODOS los campos duplicados y los retorna en una sola excepción.
     */
    private void validateUserUniqueness(String email, String username, 
                                       String documentNumber, String phone) {
        Map<String, String> duplicateFields = new HashMap<>();
        
        if (userRepository.existsByEmail(email)) {
            duplicateFields.put("email", "Ya existe un usuario con este email");
        }
        if (userRepository.existsByUsername(username)) {
            duplicateFields.put("username", "Ya existe un usuario con este username");
        }
        if (documentNumber != null && !documentNumber.isBlank() && 
            userRepository.existsByDocumentNumber(documentNumber)) {
            duplicateFields.put("documento", "Ya existe un usuario con este número de documento");
        }
        if (phone != null && !phone.isBlank() && 
            userRepository.existsByPhone(phone)) {
            duplicateFields.put("teléfono", "Ya existe un usuario con este teléfono");
        }
        
        if (!duplicateFields.isEmpty()) {
            if (duplicateFields.size() == 1) {
                // Si solo hay un campo duplicado, usar la excepción simple
                Map.Entry<String, String> entry = duplicateFields.entrySet().iterator().next();
                throw new DuplicateFieldException(entry.getKey(), 
                    entry.getKey().equals("email") ? email :
                    entry.getKey().equals("username") ? username :
                    entry.getKey().equals("documento") ? documentNumber : phone);
            } else {
                // Si hay múltiples campos duplicados, usar la excepción múltiple
                throw new MultipleDuplicateFieldsException(duplicateFields);
            }
        }
    }
    
    /**
     * Valida unicidad para actualizaciones de usuarios existentes.
     * Acumula TODOS los campos duplicados y los retorna en una sola excepción.
     */
    private void validateUpdateUniqueness(Integer userId, String newEmail, String newUsername,
                                         String newDocumentNumber, String newPhone) {
        Map<String, String> duplicateFields = new HashMap<>();
        
        if (newEmail != null && userRepository.existsByEmailAndIdNot(newEmail, userId)) {
            duplicateFields.put("email", "Ya existe un usuario con este email");
        }
        if (newUsername != null && userRepository.existsByUsernameAndIdNot(newUsername, userId)) {
            duplicateFields.put("username", "Ya existe un usuario con este username");
        }
        if (newDocumentNumber != null && !newDocumentNumber.isBlank() && 
            userRepository.existsByDocumentNumberAndIdNot(newDocumentNumber, userId)) {
            duplicateFields.put("documento", "Ya existe un usuario con este número de documento");
        }
        if (newPhone != null && !newPhone.isBlank() && 
            userRepository.existsByPhoneAndIdNot(newPhone, userId)) {
            duplicateFields.put("teléfono", "Ya existe un usuario con este teléfono");
        }
        
        if (!duplicateFields.isEmpty()) {
            if (duplicateFields.size() == 1) {
                // Si solo hay un campo duplicado, usar la excepción simple
                Map.Entry<String, String> entry = duplicateFields.entrySet().iterator().next();
                throw new DuplicateFieldException(entry.getKey(),
                    entry.getKey().equals("email") ? newEmail :
                    entry.getKey().equals("username") ? newUsername :
                    entry.getKey().equals("documento") ? newDocumentNumber : newPhone);
            } else {
                // Si hay múltiples campos duplicados, usar la excepción múltiple
                throw new MultipleDuplicateFieldsException(duplicateFields);
            }
        }
    }
    
    /**
     * Valida que el WorkRole coincida con el WorkType del usuario.
     * - Trabajadores INTERNOS solo pueden tener roles INTERNOS
     * - Trabajadores EXTERNOS solo pueden tener roles EXTERNOS
     */
    private void validateWorkRoleMatchesWorkType(Integer workRoleId, User.WorkType workType) {
        // Si alguno es null, no validar (campos opcionales)
        if (workRoleId == null || workType == null) {
            return;
        }
        
        WorkRole workRole = workRoleRepository.findById(workRoleId)
            .orElseThrow(() -> new UserNotFoundException("Work Role con ID " + workRoleId + " no encontrado"));
        
        // Mapear WorkType de User a WorkRoleType de WorkRole
        WorkRole.WorkRoleType expectedType = (workType == User.WorkType.intern) 
            ? WorkRole.WorkRoleType.intern 
            : WorkRole.WorkRoleType.extern;
        
        if (!workRole.getType().equals(expectedType)) {
            String workTypeName = (workType == User.WorkType.intern) ? "INTERNO" : "EXTERNO";
            throw new InvalidWorkRoleException(workTypeName, workRole.getName());
        }
    }
    
    /**
     * Permite a un usuario cambiar su propia contraseña.
     * Valida que la contraseña actual sea correcta antes de actualizar.
     * El email del usuario se extrae del token JWT mediante Authentication.
     * 
     * @param request contiene contraseña actual, nueva y confirmación
     * @param authenticatedUserEmail email extraído del JWT token
     * @throws InvalidPasswordException si la contraseña actual es incorrecta
     * @throws InvalidPasswordException si las contraseñas nuevas no coinciden
     * @throws UserNotFoundException si el usuario no existe
     */
    public void changePassword(co.com.ebsa.ebsa_nexus.application.dto.request.ChangePasswordRequest request, 
                               String authenticatedUserEmail) {
        log.info("User changing password: {}", authenticatedUserEmail);
        
        // 1. Validar que las nuevas contraseñas coincidan
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new InvalidPasswordException("Las contraseñas nuevas no coinciden");
        }
        
        // 2. Validar que la nueva contraseña sea diferente a la actual
        if (request.currentPassword().equals(request.newPassword())) {
            throw new InvalidPasswordException("La nueva contraseña debe ser diferente a la actual");
        }
        
        // 3. Buscar el usuario por email (extraído del token)
        User user = userRepository.findByEmail(authenticatedUserEmail)
            .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        
        // 4. Validar que la contraseña actual sea correcta
        if (!passwordEncoder.matches(request.currentPassword(), user.getPwdHash())) {
            throw new InvalidPasswordException("La contraseña actual es incorrecta");
        }
        
        // 5. Encriptar y actualizar la nueva contraseña
        user.setPwdHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        
        log.info("Password changed successfully for user: {}", authenticatedUserEmail);
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