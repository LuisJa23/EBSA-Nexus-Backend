package co.com.ebsa.ebsa_nexus.application.service;

import co.com.ebsa.ebsa_nexus.application.dto.request.ChangePasswordRequest;
import co.com.ebsa.ebsa_nexus.domain.entity.User;
import co.com.ebsa.ebsa_nexus.domain.exception.InvalidPasswordException;
import co.com.ebsa.ebsa_nexus.domain.exception.UserNotFoundException;
import co.com.ebsa.ebsa_nexus.domain.repository.UserDomainRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserManagementServiceChangePasswordTest {
    
    @Mock
    private UserDomainRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    private UserManagementService userManagementService;
    private User testUser;
    
    @BeforeEach
    void setUp() {
        userManagementService = new UserManagementService(
            userRepository, null, null, passwordEncoder
        );
        
        testUser = User.builder()
            .id(1)
            .email("user@example.com")
            .pwdHash("hashedOldPassword")
            .active(true)
            .build();
    }
    
    @Test
    void shouldChangePasswordWhenCurrentPasswordIsCorrect() {
        // Arrange
        ChangePasswordRequest request = new ChangePasswordRequest(
            "oldPassword123",
            "newPassword456",
            "newPassword456"
        );
        
        when(userRepository.findByEmail("user@example.com"))
            .thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldPassword123", "hashedOldPassword"))
            .thenReturn(true);
        when(passwordEncoder.encode("newPassword456"))
            .thenReturn("hashedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // Act
        assertDoesNotThrow(() -> {
            userManagementService.changePassword(request, "user@example.com");
        });
        
        // Assert
        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode("newPassword456");
    }
    
    @Test
    void shouldThrowExceptionWhenCurrentPasswordIsIncorrect() {
        // Arrange
        ChangePasswordRequest request = new ChangePasswordRequest(
            "wrongPassword",
            "newPassword456",
            "newPassword456"
        );
        
        when(userRepository.findByEmail("user@example.com"))
            .thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", "hashedOldPassword"))
            .thenReturn(false);
        
        // Act & Assert
        assertThrows(InvalidPasswordException.class, () -> {
            userManagementService.changePassword(request, "user@example.com");
        });
        
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void shouldThrowExceptionWhenNewPasswordsDoNotMatch() {
        // Arrange
        ChangePasswordRequest request = new ChangePasswordRequest(
            "oldPassword123",
            "newPassword456",
            "differentPassword"
        );
        
        // Act & Assert
        assertThrows(InvalidPasswordException.class, () -> {
            userManagementService.changePassword(request, "user@example.com");
        });
        
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void shouldThrowExceptionWhenNewPasswordIsSameAsOld() {
        // Arrange
        ChangePasswordRequest request = new ChangePasswordRequest(
            "samePassword",
            "samePassword",
            "samePassword"
        );
        
        // Act & Assert
        assertThrows(InvalidPasswordException.class, () -> {
            userManagementService.changePassword(request, "user@example.com");
        });
        
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        ChangePasswordRequest request = new ChangePasswordRequest(
            "oldPassword123",
            "newPassword456",
            "newPassword456"
        );
        
        when(userRepository.findByEmail("user@example.com"))
            .thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            userManagementService.changePassword(request, "user@example.com");
        });
        
        verify(userRepository, never()).save(any(User.class));
    }
}
