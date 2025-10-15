package co.com.ebsa.ebsa_nexus.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para solicitud de cambio de contraseña del usuario autenticado.
 * Requiere contraseña actual para validación de seguridad.
 */
public record ChangePasswordRequest(
    
    @NotBlank(message = "La contraseña actual es obligatoria")
    String currentPassword,
    
    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = 6, max = 20, message = "La nueva contraseña debe tener entre 6 y 20 caracteres")
    String newPassword,
    
    @NotBlank(message = "La confirmación de contraseña es obligatoria")
    String confirmPassword
) {}
