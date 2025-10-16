package co.com.ebsa.ebsa_nexus.application.crew.factories;

import co.com.ebsa.ebsa_nexus.domain.crew.entities.Crew;
import co.com.ebsa.ebsa_nexus.domain.crew.enums.CrewStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Factory para crear instancias de Crew con valores por defecto y validaciones.
 * 
 * <p>Garantiza que todas las cuadrillas se crean con:
 * <ul>
 *   <li>Estado inicial DISPONIBLE</li>
 *   <li>Timestamps correctos</li>
 *   <li>Validaciones de campos obligatorios</li>
 * </ul>
 * 
 * @author Luis Javier
 * @version 1.0
 * @since 2025-10-16
 */
@Component
public class CrewFactory {
    
    /**
     * Crea una nueva cuadrilla con valores por defecto.
     * 
     * @param name Nombre de la cuadrilla (obligatorio)
     * @param description Descripción de la cuadrilla (opcional)
     * @param createdBy ID del usuario creador (obligatorio)
     * @return Nueva instancia de Crew
     * @throws IllegalArgumentException si name o createdBy son nulos
     */
    public Crew createCrew(String name, String description, Long createdBy) {
        validateCrewCreation(name, createdBy);
        
        LocalDateTime now = LocalDateTime.now();
        
        return Crew.builder()
                .name(name.trim())
                .description(description != null ? description.trim() : null)
                .status(CrewStatus.DISPONIBLE)
                .createdBy(createdBy)
                .createdAt(now)
                .updatedAt(now)
                .deletedAt(null)
                .build();
    }
    
    /**
     * Valida los campos obligatorios para crear una cuadrilla.
     * 
     * @param name Nombre de la cuadrilla
     * @param createdBy ID del usuario creador
     * @throws IllegalArgumentException si alguna validación falla
     */
    private void validateCrewCreation(String name, Long createdBy) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Crew name cannot be null or empty");
        }
        
        if (name.trim().length() < 3) {
            throw new IllegalArgumentException("Crew name must be at least 3 characters long");
        }
        
        if (name.trim().length() > 100) {
            throw new IllegalArgumentException("Crew name cannot exceed 100 characters");
        }
        
        if (createdBy == null) {
            throw new IllegalArgumentException("Created by user ID cannot be null");
        }
        
        if (createdBy <= 0) {
            throw new IllegalArgumentException("Created by user ID must be positive");
        }
    }
}
