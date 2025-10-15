package co.com.ebsa.ebsa_nexus.application.service;

import co.com.ebsa.ebsa_nexus.application.dto.request.CreateUserRequest;
import co.com.ebsa.ebsa_nexus.application.dto.request.UpdateUserRequest;
import co.com.ebsa.ebsa_nexus.application.dto.response.UserResponse;
import co.com.ebsa.ebsa_nexus.domain.entity.Role;
import co.com.ebsa.ebsa_nexus.domain.entity.User;
import co.com.ebsa.ebsa_nexus.domain.exception.UnauthorizedOperationException;
import co.com.ebsa.ebsa_nexus.domain.exception.UserAlreadyExistsException;
import co.com.ebsa.ebsa_nexus.domain.exception.UserNotFoundException;
import co.com.ebsa.ebsa_nexus.domain.repository.UserDomainRepository;
import co.com.ebsa.ebsa_nexus.infrastructure.repository.RoleRepository;
import co.com.ebsa.ebsa_nexus.infrastructure.repository.WorkRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para UserManagementService.
 * Verifica todas las funcionalidades de gestión de usuarios,
 * validaciones de negocio y manejo de excepciones.
 */
@ExtendWith(MockitoExtension.class)
class UserManagementServiceTest {
    
    @Mock
    private UserDomainRepository userRepository;
    
    @Mock
    private RoleRepository roleRepository;
    
    @Mock
    private WorkRoleRepository workRoleRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    private UserManagementService userManagementService;
    
    private User adminUser;
    private User regularUser;
    private Role adminRole;
    private Role workerRole;
    
    @BeforeEach
    void setUp() {
        userManagementService = new UserManagementService(
            userRepository, roleRepository, workRoleRepository, passwordEncoder
        );
        
        // Setup data
        adminRole = Role.builder()
            .id(1)
            .name("ADMIN")
            .build();
            
        workerRole = Role.builder()
            .id(2)
            .name("TRABAJADOR")
            .build();
        
        adminUser = User.builder()
            .id(1)
            .email("admin@example.com")
            .username("admin")
            .roleId(1)
            .active(true)
            .build();
            
        regularUser = User.builder()
            .id(2)
            .email("user@example.com")
            .username("user")
            .roleId(2)
            .active(true)
            .build();
    }
    
    @Test
    void shouldCreateUserWhenAdminCreatesValidUser() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest(
            "newuser", "newuser@example.com", "password123",
            "New", "User", "TRABAJADOR", null, "intern",
            "12345678", "3001234567"
        );
        
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));
        when(roleRepository.findById(1)).thenReturn(Optional.of(adminRole));
        when(roleRepository.findByName("TRABAJADOR")).thenReturn(Optional.of(workerRole));
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userRepository.existsByUsername(request.username())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(3);
            return user;
        });
        
        // Act
        UserResponse result = userManagementService.createUser(request, "admin@example.com");
        
        // Assert
        assertNotNull(result);
        assertEquals(request.email(), result.email());
        assertEquals(request.username(), result.username());
        assertEquals("TRABAJADOR", result.roleName());
        assertTrue(result.active());
        
        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode(request.password());
    }
    
    @Test
    void shouldThrowExceptionWhenNonAdminTriesToCreateUser() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest(
            "newuser", "newuser@example.com", "password123",
            "New", "User", "TRABAJADOR", null, "intern",
            "12345678", "3001234567"
        );
        
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(regularUser));
        when(roleRepository.findById(2)).thenReturn(Optional.of(workerRole));
        
        // Act & Assert
        assertThrows(UnauthorizedOperationException.class, () -> {
            userManagementService.createUser(request, "user@example.com");
        });
        
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void shouldThrowExceptionWhenTryingToCreateAdminUser() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest(
            "newadmin", "newadmin@example.com", "password123",
            "New", "Admin", "ADMIN", null, "intern",
            "12345678", "3001234567"
        );
        
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));
        when(roleRepository.findById(1)).thenReturn(Optional.of(adminRole));
        
        // Act & Assert
        assertThrows(UnauthorizedOperationException.class, () -> {
            userManagementService.createUser(request, "admin@example.com");
        });
        
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void shouldThrowExceptionWhenUserAlreadyExistsByEmail() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest(
            "newuser", "existing@example.com", "password123",
            "New", "User", "TRABAJADOR", null, "intern",
            "12345678", "3001234567"
        );
        
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));
        when(roleRepository.findById(1)).thenReturn(Optional.of(adminRole));
        when(userRepository.existsByEmail(request.email())).thenReturn(true);
        
        // Act & Assert
        assertThrows(UserAlreadyExistsException.class, () -> {
            userManagementService.createUser(request, "admin@example.com");
        });
        
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void shouldUpdateUserWhenAdminUpdatesValidUser() {
        // Arrange
        UpdateUserRequest request = new UpdateUserRequest(
            "updateduser", "updated@example.com", null,
            "Updated", "User", null, null, null,
            null, "3009876543", true
        );
        
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));
        when(roleRepository.findById(1)).thenReturn(Optional.of(adminRole));
        when(userRepository.findById(2)).thenReturn(Optional.of(regularUser));
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userRepository.existsByUsername(request.username())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(regularUser);
        when(roleRepository.findById(2)).thenReturn(Optional.of(workerRole));
        
        // Act
        UserResponse result = userManagementService.updateUser(2, request, "admin@example.com");
        
        // Assert
        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }
    
    @Test
    void shouldThrowExceptionWhenAdminTriesToDeactivateOwnAccount() {
        // Arrange
        UpdateUserRequest request = new UpdateUserRequest(
            null, null, null, null, null, null, null, null,
            null, null, false
        );
        
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));
        when(roleRepository.findById(1)).thenReturn(Optional.of(adminRole));
        when(userRepository.findById(1)).thenReturn(Optional.of(adminUser));
        
        // Act & Assert
        assertThrows(UnauthorizedOperationException.class, () -> {
            userManagementService.updateUser(1, request, "admin@example.com");
        });
    }
    
    @Test
    void shouldDeactivateUserWhenAdminDeactivatesOtherUser() {
        // Arrange
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));
        when(roleRepository.findById(1)).thenReturn(Optional.of(adminRole));
        when(userRepository.findById(2)).thenReturn(Optional.of(regularUser));
        when(userRepository.save(any(User.class))).thenReturn(regularUser);
        
        // Act
        assertDoesNotThrow(() -> {
            userManagementService.deactivateUser(2, "admin@example.com");
        });
        
        // Assert
        verify(userRepository, times(1)).save(any(User.class));
    }
    
    @Test
    void shouldThrowExceptionWhenAdminTriesToDeactivateOwnAccountDirectly() {
        // Arrange
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));
        when(roleRepository.findById(1)).thenReturn(Optional.of(adminRole));
        when(userRepository.findById(1)).thenReturn(Optional.of(adminUser));
        
        // Act & Assert
        assertThrows(UnauthorizedOperationException.class, () -> {
            userManagementService.deactivateUser(1, "admin@example.com");
        });
        
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void shouldGetUserByIdWhenAdminRequestsValidUser() {
        // Arrange
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));
        when(roleRepository.findById(1)).thenReturn(Optional.of(adminRole));
        when(userRepository.findById(2)).thenReturn(Optional.of(regularUser));
        when(roleRepository.findById(2)).thenReturn(Optional.of(workerRole));
        
        // Act
        UserResponse result = userManagementService.getUserById(2, "admin@example.com");
        
        // Assert
        assertNotNull(result);
        assertEquals(regularUser.getEmail(), result.email());
        assertEquals("TRABAJADOR", result.roleName());
    }
    
    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));
        when(roleRepository.findById(1)).thenReturn(Optional.of(adminRole));
        when(userRepository.findById(999)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            userManagementService.getUserById(999, "admin@example.com");
        });
    }
    
    @Test
    void shouldGetAllUsersWhenAdminRequests() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(Arrays.asList(adminUser, regularUser));
        
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));
        when(roleRepository.findById(1)).thenReturn(Optional.of(adminRole));
        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(roleRepository.findById(anyInt())).thenReturn(Optional.of(workerRole));
        
        // Act
        Page<UserResponse> result = userManagementService.getAllUsers(pageable, "admin@example.com");
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
    }
}