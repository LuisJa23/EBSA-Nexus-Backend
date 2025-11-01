package co.com.ebsa.ebsa_nexus.application.dto.request.novelty;

import co.com.ebsa.ebsa_nexus.domain.enums.NoveltyStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO para actualizar el estado de una novedad.
 * 
 * @author EBSA Nexus Team
 * @version 1.0
 * @since 2025-10-29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateNoveltyStatusRequest {
    
    /**
     * Nuevo estado de la novedad.
     * Valores permitidos: CREADA, EN_CURSO, COMPLETADA, CERRADA, CANCELADA
     */
    @NotNull(message = "El estado es obligatorio")
    private NoveltyStatus status;
    
    /**
     * Notas opcionales sobre el cambio de estado
     */
    private String notes;
}
