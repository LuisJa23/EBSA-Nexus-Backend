package co.com.ebsa.ebsa_nexus.presentation.controller;

import co.com.ebsa.ebsa_nexus.application.dto.request.CreateNoveltyReportRequest;
import co.com.ebsa.ebsa_nexus.application.dto.response.NoveltyReportResponse;
import co.com.ebsa.ebsa_nexus.application.service.novelty.NoveltyResolutionReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/novelty-reports")
@RequiredArgsConstructor
public class NoveltyReportController {
    
    private final NoveltyResolutionReportService reportService;
    
    @PostMapping
    public ResponseEntity<NoveltyReportResponse> createReport(
            @Valid @RequestBody CreateNoveltyReportRequest request,
            Authentication authentication) {
        
        // Permitir acceso sin autenticación para desarrollo/pruebas
        String username = authentication != null ? authentication.getName() : "anonymous";
        log.info("Request to create report for novelty {} by user {}",
            request.getNoveltyId(), username);
        
        Long userId = getUserIdFromAuthentication(authentication);
        NoveltyReportResponse response = reportService.createReport(request, userId);
        
        log.info("Report created successfully with ID: {}", response.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/by-novelty/{noveltyId}")
    public ResponseEntity<NoveltyReportResponse> getReportByNoveltyId(
            @PathVariable Long noveltyId) {
        
        log.debug("Request to get report for novelty {}", noveltyId);
        
        return reportService.getReportByNoveltyId(noveltyId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{reportId}")
    public ResponseEntity<NoveltyReportResponse> getReportById(
            @PathVariable Long reportId) {
        
        log.debug("Request to get report {}", reportId);
        NoveltyReportResponse response = reportService.getReportById(reportId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Obtiene todos los reportes generados por un usuario.
     * 
     * @param userId ID del usuario
     * @return Lista de reportes
     */
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<NoveltyReportResponse>> getReportsByUser(
            @PathVariable Long userId) {
        
        log.debug("Request to get reports by user {}", userId);
        List<NoveltyReportResponse> reports = reportService.getReportsByUser(userId);
        return ResponseEntity.ok(reports);
    }
    
    /**
     * Obtiene los reportes del usuario autenticado.
     * 
     * @param authentication Información del usuario autenticado
     * @return Lista de reportes
     */
    @GetMapping("/my-reports")
    public ResponseEntity<List<NoveltyReportResponse>> getMyReports(Authentication authentication) {
        log.debug("Request to get reports for authenticated user");
        Long userId = getUserIdFromAuthentication(authentication);
        List<NoveltyReportResponse> reports = reportService.getReportsByUser(userId);
        return ResponseEntity.ok(reports);
    }
    
    // ========== Métodos auxiliares ==========
    
    /**
     * Extrae el ID del usuario desde el objeto Authentication.
     * 
     * NOTA: Este método debe ajustarse según la implementación real de seguridad.
     * 
     * @param authentication Objeto de autenticación
     * @return ID del usuario
     */
    private Long getUserIdFromAuthentication(Authentication authentication) {
        // ⚠️ MODO DESARROLLO: Permitir acceso sin autenticación
        if (authentication == null) {
            log.warn("No authentication found - using default user ID 1 for development");
            return 1L; // Usuario por defecto para pruebas
        }
        
        // IMPLEMENTACIÓN TEMPORAL
        // Ajustar según la estructura real del UserDetails/Principal
        try {
            // Opción 1: Si el principal es el ID directamente
            if (authentication.getPrincipal() instanceof Long) {
                return (Long) authentication.getPrincipal();
            }
            
            // Opción 2: Si el principal tiene un método getId()
            Object principal = authentication.getPrincipal();
            if (principal != null) {
                try {
                    var method = principal.getClass().getMethod("getId");
                    return (Long) method.invoke(principal);
                } catch (Exception e) {
                    log.warn("Could not extract user ID from authentication", e);
                }
            }
            
            // Fallback: retornar un ID por defecto (SOLO PARA DESARROLLO)
            log.warn("Using fallback user ID. This should be fixed in production!");
            return 1L;
            
        } catch (Exception e) {
            log.error("Error extracting user ID from authentication", e);
            throw new RuntimeException("No se pudo determinar el usuario autenticado");
        }
    }
}
