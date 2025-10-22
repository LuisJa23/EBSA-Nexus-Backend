package co.com.ebsa.ebsa_nexus.infrastructure.config;

import co.com.ebsa.ebsa_nexus.infrastructure.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Filtro JWT para procesar tokens de autenticación en cada petición HTTP.
 * 
 * Este filtro:
 * 1. Extrae el token JWT del header Authorization
 * 2. Valida el token usando JwtUtil
 * 3. Extrae el email y rol del usuario
 * 4. Establece la autenticación en el SecurityContext
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        String authorizationHeader = request.getHeader("Authorization");
        log.info("Processing request: {} {} with Authorization header: {}", 
                request.getMethod(), request.getRequestURI(), 
                authorizationHeader != null ? "Bearer ***" : "null");
        
        // Verificar si existe el header Authorization con Bearer token
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7); // Remover "Bearer "
            
            try {
                // Validar el token
                if (jwtUtil.validateToken(token)) {
                    String email = jwtUtil.extractEmail(token);
                    String role = jwtUtil.extractRole(token);
                    Long userId = jwtUtil.extractUserId(token);
                    
                    log.info("JWT validation successful for email: {} with role: {} and userId: {}", email, role, userId);
                    
                    if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        // Establecer userId como atributo del request si existe
                        if (userId != null) {
                            request.setAttribute("userId", userId);
                            log.debug("Set userId attribute: {}", userId);
                        }
                        
                        // Crear la autenticación con el rol
                        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
                        UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(
                                email, 
                                null, 
                                Collections.singletonList(authority)
                            );
                        
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        
                        log.info("JWT authentication successful for user: {} with role: {}", email, role);
                    }
                } else {
                    log.warn("Invalid JWT token received");
                }
            } catch (Exception e) {
                log.error("JWT token processing error: {}", e.getMessage(), e);
                // No establecer autenticación - dejar que Spring Security maneje el error
            }
        } else {
            log.info("No Authorization header found or not Bearer token");
        }
        
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        // No filtrar rutas públicas
        return path.startsWith("/auth/") || 
               path.startsWith("/actuator/") || 
               path.equals("/health") || 
               path.equals("/error");
    }
}