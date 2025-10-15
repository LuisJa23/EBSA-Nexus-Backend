package co.com.ebsa.ebsa_nexus.presentation.controller;

import co.com.ebsa.ebsa_nexus.application.dto.request.ChangePasswordRequest;
import co.com.ebsa.ebsa_nexus.application.service.UserManagementService;
import co.com.ebsa.ebsa_nexus.domain.exception.InvalidPasswordException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserManagementController.class)
class UserManagementControllerChangePasswordTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserManagementService userManagementService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "user@example.com")
    void shouldChangePasswordWhenValidRequest() throws Exception {
        // Arrange
        ChangePasswordRequest request = new ChangePasswordRequest(
            "oldPassword123",
            "newPassword456",
            "newPassword456"
        );
        
        doNothing().when(userManagementService)
            .changePassword(any(ChangePasswordRequest.class), anyString());

        // Act & Assert
        mockMvc.perform(patch("/api/users/me/change-password")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user@example.com")
    void shouldReturn400WhenCurrentPasswordIsIncorrect() throws Exception {
        // Arrange
        ChangePasswordRequest request = new ChangePasswordRequest(
            "wrongPassword",
            "newPassword456",
            "newPassword456"
        );
        
        doThrow(new InvalidPasswordException("La contraseña actual es incorrecta"))
            .when(userManagementService)
            .changePassword(any(ChangePasswordRequest.class), anyString());

        // Act & Assert
        mockMvc.perform(patch("/api/users/me/change-password")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@example.com")
    void shouldReturn400WhenRequestHasInvalidData() throws Exception {
        // Arrange
        ChangePasswordRequest invalidRequest = new ChangePasswordRequest(
            "",
            "short",
            "short"
        );

        // Act & Assert
        mockMvc.perform(patch("/api/users/me/change-password")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn401WhenUnauthenticated() throws Exception {
        // Arrange
        ChangePasswordRequest request = new ChangePasswordRequest(
            "oldPassword123",
            "newPassword456",
            "newPassword456"
        );

        // Act & Assert
        mockMvc.perform(patch("/api/users/me/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
