package co.com.ebsa.ebsa_nexus.integration;

import co.com.ebsa.ebsa_nexus.application.dto.request.auth.LoginRequestDTO;
import co.com.ebsa.ebsa_nexus.domain.entity.Role;
import co.com.ebsa.ebsa_nexus.domain.entity.User;
import co.com.ebsa.ebsa_nexus.infrastructure.persistence.jpa.repositories.RoleRepository;
import co.com.ebsa.ebsa_nexus.infrastructure.persistence.jpa.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserManagementIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;
    @Autowired RoleRepository roleRepository;
    @Autowired PasswordEncoder passwordEncoder;

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setup() throws Exception {
        userRepository.deleteAll();
        roleRepository.deleteAll();

        Role adminRole = roleRepository.save(Role.builder().name("ADMIN").build());
        Role userRole = roleRepository.save(Role.builder().name("USER").build());

        User admin = User.builder()
                .uuid(UUID.randomUUID().toString())
                .username("admin1")
                .email("admin@example.com")
                .pwdHash(passwordEncoder.encode("AdminPass1!"))
                .firstName("A")
                .lastName("D")
                .roleId(adminRole.getId())
                .phone("111")
                .active(true)
                .build();
        userRepository.save(admin);

        User user = User.builder()
                .uuid(UUID.randomUUID().toString())
                .username("user1")
                .email("user@example.com")
                .pwdHash(passwordEncoder.encode("UserPass1!"))
                .firstName("U")
                .lastName("S")
                .roleId(userRole.getId())
                .phone("222")
                .active(true)
                .build();
        userRepository.save(user);

        // login admin
        LoginRequestDTO adminReq = new LoginRequestDTO("admin@example.com", "AdminPass1!");
        String adminJson = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminReq)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        adminToken = objectMapper.readTree(adminJson).get("token").asText();
        assertThat(adminToken).isNotBlank();

        // login user
        LoginRequestDTO userReq = new LoginRequestDTO("user@example.com", "UserPass1!");
        String userJson = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userReq)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        userToken = objectMapper.readTree(userJson).get("token").asText();
        assertThat(userToken).isNotBlank();
    }

    @Test
    void public_workers_endpoint_is_accessible_without_auth() throws Exception {
        mockMvc.perform(get("/api/public/workers"))
                .andExpect(status().isOk());
    }

    @Test
    void users_list_requires_admin_role() throws Exception {
        // no token -> 401
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
        // user token -> 403
        mockMvc.perform(get("/api/users").header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
        // admin token -> 200
        mockMvc.perform(get("/api/users").header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }
}
