package co.com.ebsa.ebsa_nexus.application.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import co.com.ebsa.ebsa_nexus.application.dto.request.auth.LoginRequestDTO;
import co.com.ebsa.ebsa_nexus.application.dto.response.LoginResponseDTO;
import co.com.ebsa.ebsa_nexus.domain.entity.User;
import co.com.ebsa.ebsa_nexus.domain.exception.auth.AuthenticationException;
import co.com.ebsa.ebsa_nexus.infrastructure.persistence.jpa.repositories.UserRepository;
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
            User user = userRepository.findByEmailAndActiveTrue(request.email())
                .orElseThrow(() -> new AuthenticationException("Email no encontrado o usuario inactivo"));
            
            if (!passwordEncoder.matches(request.password(), user.getPwdHash())) {
                throw new AuthenticationException("Credenciales inválidas");
            }
            
            // Update last login
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
            
            // Generar token con rol (usar el nombre del rol de la entidad Role)
            String roleName = user.getRole().getName();
            String workRoleName = user.getWorkRole() != null ? user.getWorkRole().getName() : null;
            
            // Convertir WorkType enum a string (intern o extern en minúsculas)
            String workType = user.getWorkType() != null ? user.getWorkType().name() : null;
            
            String token = jwtUtil.generateToken(user.getEmail(), roleName, user.getId());
            
            return new LoginResponseDTO(
                user.getId(), 
                token, 
                user.getEmail(), 
                user.getUsername(), 
                roleName, 
                workRoleName, 
                workType
            );
        } catch (Exception e) {
            throw new AuthenticationException("Error durante la autenticación: " + e.getMessage());
        }
    }
}