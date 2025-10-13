package co.com.ebsa.ebsa_nexus.application.service;


import org.springframework.stereotype.Service;

import co.com.ebsa.ebsa_nexus.infrastructure.utils.JwtUtil;

@Service
public class AuthorizationApplicationService {

    private final JwtUtil jwtUtil;

    public AuthorizationApplicationService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    public String extractEmail(String token) {
        return jwtUtil.extractEmail(token);
    }

    /**
     * @deprecated Use extractEmail instead
     */
    @Deprecated
    public String extractUsername(String token) {
        return jwtUtil.extractEmail(token);
    }

    public String extractRole(String token) {
        return jwtUtil.extractRole(token);
    }
}
