package co.com.ebsa.ebsa_nexus.application.dto.request;

import co.com.ebsa.ebsa_nexus.domain.enums.ResolutionStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de solicitud para crear un reporte de novedad.
 * 
 * @author EBSA Nexus Team
 * @version 1.0
 * @since 2025-10-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateNoveltyReportRequest {
    
    @NotNull(message = "El ID de la novedad es obligatorio")
    private Long noveltyId;
    
    @NotBlank(message = "El contenido del reporte es obligatorio")
    private String reportContent;
    
    private String observations;
    
    @NotNull(message = "La fecha de inicio del trabajo es obligatoria")
    private LocalDateTime workStartDate;
    
    @NotNull(message = "La fecha de finalización del trabajo es obligatoria")
    private LocalDateTime workEndDate;
    
    @NotNull(message = "El estado de resolución es obligatorio")
    private ResolutionStatus resolutionStatus;
    
    @NotEmpty(message = "Debe especificar al menos un participante que resolvió la novedad")
    @Valid
    private List<ParticipantRequest> participants;
    
    /**
     * DTO interno para los participantes.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParticipantRequest {
        
        @NotNull(message = "El ID del usuario es obligatorio")
        private Long userId;
    }
}
