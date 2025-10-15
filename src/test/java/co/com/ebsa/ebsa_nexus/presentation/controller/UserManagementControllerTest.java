package co.com.ebsa.ebsa_nexus.presentation.controller;

import co.com.ebsa.ebsa_nexus.application.dto.request.CreateUserRequest;
import co.com.ebsa.ebsa_nexus.application.dto.request.UpdateUserRequest;
import co.com.ebsa.ebsa_nexus.application.dto.response.UserResponse;
import co.com.ebsa.ebsa_nexus.application.service.UserManagementService;
import co.com.ebsa.ebsa_nexus.domain.entity.User.WorkType;
import co.com.ebsa.ebsa_nexus.domain.exception.UserNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración para UserManagementController.
 * Verifica el comportamiento completo de los endpoints REST,
 * incluyendo validaciones, serialización y respuestas HTTP.
 */
@WebMvcTest(UserManagementController.class)
class UserManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserManagementService userManagementService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserResponse userResponse;
    private CreateUserRequest createRequest;
    private UpdateUserRequest updateRequest;

    @BeforeEach
    void setUp() {
        userResponse = new UserResponse(
            1, "uuid-123", "testuser", "test@example.com",
            "Test", "User", "TRABAJADOR", null, WorkType.intern,
            "12345678", "3001234567", true,
            LocalDateTime.now(), LocalDateTime.now(), null
        );

        createRequest = new CreateUserRequest(
            "newuser", "newuser@example.com", "password123",
            "New", "User", "TRABAJADOR", null, "intern",
            "12345678", "3001234567"
        );

        updateRequest = new UpdateUserRequest(
            "updateduser", "updated@example.com", null,
            "Updated", "User", null, null, null,
            null, "3009876543", true
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateUserWhenValidRequestFromAdmin() throws Exception {
        // Arrange
        when(userManagementService.createUser(any(CreateUserRequest.class), anyString()))
            .thenReturn(userResponse);

        // Act & Assert
        mockMvc.perform(post("/api/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value(userResponse.username()))
                .andExpect(jsonPath("$.email").value(userResponse.email()))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenNonAdminTriesToCreateUser() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn401WhenUnauthenticatedUserTriesToCreateUser() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturn400WhenCreateUserRequestHasInvalidData() throws Exception {
        // Arrange
        CreateUserRequest invalidRequest = new CreateUserRequest(
            "", "", "", "", "", null, null, null, null, ""
        );

        // Act & Assert
        mockMvc.perform(post("/api/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateUserWhenValidRequestFromAdmin() throws Exception {
        // Arrange
        when(userManagementService.updateUser(eq(1), any(UpdateUserRequest.class), anyString()))
            .thenReturn(userResponse);

        // Act & Assert
        mockMvc.perform(put("/api/users/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value(userResponse.username()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturn404WhenUpdatingNonExistentUser() throws Exception {
        // Arrange
        when(userManagementService.updateUser(eq(999), any(UpdateUserRequest.class), anyString()))
            .thenThrow(new UserNotFoundException(999));

        // Act & Assert
        mockMvc.perform(put("/api/users/999")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeactivateUserWhenValidRequestFromAdmin() throws Exception {
        // Act & Assert
        mockMvc.perform(patch("/api/users/1/deactivate")
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenNonAdminTriesToDeactivateUser() throws Exception {
        // Act & Assert
        mockMvc.perform(patch("/api/users/1/deactivate")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetUserByIdWhenValidRequestFromAdmin() throws Exception {
        // Arrange
        when(userManagementService.getUserById(eq(1), anyString()))
            .thenReturn(userResponse);

        // Act & Assert
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userResponse.id()))
                .andExpect(jsonPath("$.email").value(userResponse.email()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturn404WhenGettingNonExistentUser() throws Exception {
        // Arrange
        when(userManagementService.getUserById(eq(999), anyString()))
            .thenThrow(new UserNotFoundException(999));

        // Act & Assert
        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetAllUsersWhenValidRequestFromAdmin() throws Exception {
        // Arrange
        Page<UserResponse> userPage = new PageImpl<>(
            Arrays.asList(userResponse), PageRequest.of(0, 20), 1
        );
        when(userManagementService.getAllUsers(any(), anyString()))
            .thenReturn(userPage);

        // Act & Assert
        mockMvc.perform(get("/api/users")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(userResponse.id()))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenNonAdminTriesToListUsers() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());
    }
}