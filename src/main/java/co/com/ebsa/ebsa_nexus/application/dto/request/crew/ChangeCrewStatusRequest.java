package co.com.ebsa.ebsa_nexus.application.dto.request.crew;

import co.com.ebsa.ebsa_nexus.domain.enums.CrewStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for changing crew status
 * Used to transition crew between DISPONIBLE, EN_ATENCION, and INACTIVO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeCrewStatusRequest {
    
    /**
     * New status for the crew
     * Must be one of: DISPONIBLE, EN_ATENCION, INACTIVO
     */
    @NotNull(message = "New status is required")
    private CrewStatus newStatus;
}
