// SecurityConfig.java - VERSIÓN CORREGIDA
// Copiar y reemplazar el contenido de:
// src/main/java/co/com/ebsa/ebsa_nexus/infrastructure/config/SecurityConfig.java

package co.com.ebsa.ebsa_nexus.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;



@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .httpBasic(basic -> basic.disable())
            .formLogin(form -> form.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(authz -> authz
                .requestMatchers(
                    "/api/auth/**",
                    "/auth/**", 
                    "/actuator/**", 
                    "/health", 
                    "/error",
                    "/api/public/**",
                    "/areas",
                    "/api/work-roles",
                    "/api/work-roles/**", // Permitir acceso público a todos los endpoints de work-roles
                    "/api/v1/notifications",
                    "/api/v1/notifications/**", // Permitir acceso público a notificaciones (para pruebas)
                    "/api/v1/novelties",
                    "/api/v1/novelties/**" // Permitir acceso público a novedades (para pruebas)
                ).permitAll()
                .requestMatchers("/api/users/me").authenticated()  // Permitir a cualquier usuario autenticado
                .requestMatchers("/api/users/**").hasRole("ADMIN") // Solo ADMIN para el resto de endpoints
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // ════════════════════════════════════════════════════════════════
        // ⚠️ CAMBIO PRINCIPAL: Agregar IP de red local
        // ════════════════════════════════════════════════════════════════
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:4200",
            "http://localhost:8084",
            "http://192.168.1.38:8080",     // ← AGREGADO: IP de red local con puerto
            "http://192.168.1.38"           // ← AGREGADO: IP de red local sin puerto
        ));
        
        // ════════════════════════════════════════════════════════════════
        // ⚠️ CAMBIO SECUNDARIO: Agregar PATCH a métodos permitidos
        // ════════════════════════════════════════════════════════════════
        configuration.setAllowedMethods(Arrays.asList(
            "GET", 
            "POST", 
            "PUT", 
            "PATCH",    // ← AGREGADO: Método PATCH
            "DELETE", 
            "OPTIONS"
        ));
        
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", 
            "Content-Type", 
            "X-Requested-With", 
            "Accept"
        ));
        
        configuration.setAllowCredentials(false);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
