package co.com.ebsa.ebsa_nexus.application.dto.request.novelty;

import co.com.ebsa.ebsa_nexus.domain.enums.NoveltyReason;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para la creación de una novedad.
 * Mapea exactamente los campos del formulario.
 * TODOS LOS CAMPOS SON OBLIGATORIOS excepto address y observations.
 * 
 * @author EBSA Nexus Team
 * @version 2.0
 * @since 2025-10-22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateNoveltyRequest {
    
    @NotNull(message = "El área es obligatoria")
    @Positive(message = "El área debe ser un ID válido")
    private Long areaId;

    @NotNull(message = "El motivo es obligatorio")
    private NoveltyReason reason;

    @NotBlank(message = "El número de cuenta es obligatorio")
    @Size(max = 50, message = "El número de cuenta no puede exceder 50 caracteres")
    private String accountNumber;

    @NotBlank(message = "El número del medidor es obligatorio")
    @Size(max = 50, message = "El número del medidor no puede exceder 50 caracteres")
    private String meterNumber;

    @NotNull(message = "La lectura activa es obligatoria")
    @DecimalMin(value = "0.0", message = "La lectura activa debe ser mayor o igual a 0")
    private BigDecimal activeReading;

    @NotNull(message = "La lectura reactiva es obligatoria")
    @DecimalMin(value = "0.0", message = "La lectura reactiva debe ser mayor o igual a 0")
    private BigDecimal reactiveReading;

    @NotNull(message = "El municipio es obligatorio")
    @Positive(message = "El ID del municipio debe ser válido")
    private Long locationId;

    @Size(max = 255, message = "La dirección no puede exceder 255 caracteres")
    private String address;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 10, max = 1000, message = "La descripción debe tener entre 10 y 1000 caracteres")
    private String description;

    @Size(max = 1000, message = "Las observaciones no pueden exceder 1000 caracteres")
    private String observations;

    // Archivos de imágenes para subir
    @Size(max = 10, message = "Máximo 10 imágenes permitidas")
    private List<MultipartFile> images;
}
