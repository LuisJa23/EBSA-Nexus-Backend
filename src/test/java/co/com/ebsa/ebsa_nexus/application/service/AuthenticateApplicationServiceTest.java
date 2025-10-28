package co.com.ebsa.ebsa_nexus.application.service;

import co.com.ebsa.ebsa_nexus.application.dto.request.auth.LoginRequestDTO;
import co.com.ebsa.ebsa_nexus.application.dto.response.LoginResponseDTO;
import co.com.ebsa.ebsa_nexus.domain.entity.Role;
import co.com.ebsa.ebsa_nexus.domain.entity.User;
import co.com.ebsa.ebsa_nexus.domain.exception.auth.AuthenticationException;
import co.com.ebsa.ebsa_nexus.infrastructure.persistence.jpa.repositories.UserRepository;
import co.com.ebsa.ebsa_nexus.infrastructure.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class AuthenticateApplicationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticateApplicationService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new AuthenticateApplicationService(userRepository, jwtUtil, passwordEncoder);
    }

    @Test
    void login_success_returnsTokenAndUpdatesLastLogin() {
        User user = User.builder()
                .id(1L)
                .email("user@example.com")
                .username("user1")
                .pwdHash("hashed")
                .roleId(10L)
                .role(Role.builder().id(10L).name("ADMIN").build())
                .build();

        when(userRepository.findByEmailAndActiveTrue("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "hashed")).thenReturn(true);
        when(jwtUtil.generateToken("user@example.com", "ADMIN", 1L)).thenReturn("jwt-token");

        LoginRequestDTO req = new LoginRequestDTO("user@example.com", "password");
        LoginResponseDTO resp = service.login(req);

        assertThat(resp.token()).isEqualTo("jwt-token");
        assertThat(resp.email()).isEqualTo("user@example.com");
        assertThat(resp.role()).isEqualTo("ADMIN");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getLastLogin()).isNotNull();
    }

    @Test
    void login_invalidPassword_throwsAuthenticationException() {
        User user = User.builder()
                .id(1L)
                .email("user@example.com")
                .pwdHash("hashed")
                .role(Role.builder().name("USER").build())
                .build();
        when(userRepository.findByEmailAndActiveTrue("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("bad", "hashed")).thenReturn(false);

        LoginRequestDTO req = new LoginRequestDTO("user@example.com", "bad");

        assertThatThrownBy(() -> service.login(req))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Credenciales inválidas");

        verify(userRepository, never()).save(any());
    }

    @Test
    void login_notFoundOrInactive_throwsAuthenticationException() {
        when(userRepository.findByEmailAndActiveTrue("missing@example.com")).thenReturn(Optional.empty());

        LoginRequestDTO req = new LoginRequestDTO("missing@example.com", "password");

        assertThatThrownBy(() -> service.login(req))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Email no encontrado o usuario inactivo");
    }
}
