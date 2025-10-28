package co.com.ebsa.ebsa_nexus.system;

import co.com.ebsa.ebsa_nexus.application.dto.request.auth.LoginRequestDTO;
import co.com.ebsa.ebsa_nexus.domain.entity.Role;
import co.com.ebsa.ebsa_nexus.domain.entity.User;
import co.com.ebsa.ebsa_nexus.infrastructure.persistence.jpa.repositories.RoleRepository;
import co.com.ebsa.ebsa_nexus.infrastructure.persistence.jpa.repositories.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
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
class AuthSystemTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;
    @Autowired RoleRepository roleRepository;
    @Autowired PasswordEncoder passwordEncoder;

    private String email;
    private String password;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        roleRepository.deleteAll();

        Role userRole = roleRepository.save(Role.builder().name("USER").build());

        email = "worker@example.com";
        password = "Secret1!";

        User u = User.builder()
                .uuid(UUID.randomUUID().toString())
                .username("worker1")
                .email(email)
                .pwdHash(passwordEncoder.encode(password))
                .firstName("W")
                .lastName("K")
                .roleId(userRole.getId())
                .phone("555")
                .active(true)
                .build();
        userRepository.save(u);
    }

    @Test
    void login_and_access_me_flow() throws Exception {
        LoginRequestDTO req = new LoginRequestDTO(email, password);
        String body = objectMapper.writeValueAsString(req);

        String loginJson = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode node = objectMapper.readTree(loginJson);
        String token = node.get("token").asText();
        assertThat(token).isNotBlank();

        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void access_me_without_token_is_unauthorized() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }
}
