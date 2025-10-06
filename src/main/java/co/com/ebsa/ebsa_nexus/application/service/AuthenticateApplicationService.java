package co.com.ebsa.ebsa_nexus.application.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import co.com.ebsa.ebsa_nexus.application.dto.request.LoginRequestDTO;
import co.com.ebsa.ebsa_nexus.application.dto.response.LoginResponseDTO;
import co.com.ebsa.ebsa_nexus.domain.entity.User;
import co.com.ebsa.ebsa_nexus.domain.exception.AuthenticationException;
import co.com.ebsa.ebsa_nexus.infrastructure.repository.UserRepository;
import co.com.ebsa.ebsa_nexus.infrastructure.utils.JwtUtil;

import java.time.LocalDateTime;

@Service
public class AuthenticateApplicationService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthenticateApplicationService(UserRepository userRepository,
                                          JwtUtil jwtUtil,
                                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponseDTO login(LoginRequestDTO request) {
        try {
            User user = userRepository.findByUsernameAndActiveTrue(request.username())
                    .orElseThrow(() -> new AuthenticationException("Usuario no encontrado o inactivo"));

            if (!passwordEncoder.matches(request.password(), user.getPwdHash())) {
                throw new AuthenticationException("Credenciales inválidas");
            }

            // Update last login
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);

            // Generar token con rol (usar el nombre del rol de la entidad Role)
            String roleName = user.getRole().getName();
            String token = jwtUtil.generateToken(user.getUsername(), roleName);

            return new LoginResponseDTO(token, user.getUsername(), roleName);

        } catch (Exception e) {
            throw new AuthenticationException("Error durante la autenticación: " + e.getMessage());
        }
    }
}