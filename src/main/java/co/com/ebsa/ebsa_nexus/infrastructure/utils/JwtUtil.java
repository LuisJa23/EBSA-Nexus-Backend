package co.com.ebsa.ebsa_nexus.infrastructure.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.nio.charset.StandardCharsets;

/**
 * Utilidad para operaciones con tokens JWT.
 * 
 * Proporciona funcionalidades para generar, validar tokens JWT, extraer información
 * del usuario y verificar la integridad y vigencia de los tokens.
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpirationMs;

    @Value("${jwt.issuer:ebsa-nexus-backend}")
    private String jwtIssuer;

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(email)
                .setIssuer(jwtIssuer)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken(String email, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuer(jwtIssuer)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Genera un token JWT completo con email, rol y userId.
     * 
     * @param email el email del usuario
     * @param role el rol del usuario
     * @param userId el ID del usuario
     * @return el token JWT generado
     */
    public String generateToken(String email, String role, Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .claim("userId", userId)
                .setIssuer(jwtIssuer)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Valida si un token JWT es válido verificando issuer y expiración.
     * 
     * @param token el token JWT a validar (no debe ser null o vacío)
     * @return true si el token es válido, false en caso contrario
     */
    public boolean validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        
        try {
            Claims claims = getClaims(token);
            
            // Verificar issuer
            if (!jwtIssuer.equals(claims.getIssuer())) {
                return false;
            }
            
            // Verificar expiración
            if (isTokenExpired(token)) {
                return false;
            }
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extrae el email (subject) del token JWT.
     * 
     * @param token el token JWT del cual extraer el email
     * @return el email o null si el token es inválido o vacío
     */
    public String extractEmail(String token) {
        if (token == null || token.trim().isEmpty()) {
            return null;
        }
        return getClaims(token).getSubject();
    }

    /**
     * Método de compatibilidad - extrae el email del token JWT.
     * @deprecated Use extractEmail instead
     */
    @Deprecated
    public String extractUsername(String token) {
        return extractEmail(token);
    }

    /**
     * Extrae el rol del usuario del token JWT.
     * 
     * @param token el token JWT del cual extraer el rol
     * @return el rol del usuario o null si el token es inválido o vacío
     */
    public String extractRole(String token) {
        if (token == null || token.trim().isEmpty()) {
            return null;
        }
        return getClaims(token).get("role", String.class);
    }

    /**
     * Extrae el userId del token JWT.
     * 
     * @param token el token JWT del cual extraer el userId
     * @return el userId o null si el token es inválido, vacío o no contiene userId
     */
    public Long extractUserId(String token) {
        if (token == null || token.trim().isEmpty()) {
            return null;
        }
        Integer userId = getClaims(token).get("userId", Integer.class);
        return userId != null ? userId.longValue() : null;
    }

    /**
     * Verifica si el token JWT ha expirado.
     * 
     * @param token el token JWT a verificar
     * @return true si el token ha expirado, false en caso contrario
     */
    public boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
