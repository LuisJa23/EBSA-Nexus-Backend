package co.com.ebsa.ebsa_nexus.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO para que un usuario actualice su propio perfil.
 * Solo permite actualizar campos básicos de información personal.
 * No permite cambiar email, username, password, documentNumber, roles ni permisos.
 */
public record UpdateOwnProfileRequest(
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 45, message = "El nombre debe tener entre 2 y 45 caracteres")
    String firstName,
    
    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 45, message = "El apellido debe tener entre 2 y 45 caracteres")
    String lastName,
    
    @NotBlank(message = "El teléfono es obligatorio")
    @Size(min = 10, max = 45, message = "El teléfono debe tener entre 10 y 45 caracteres")
    @Pattern(regexp = "^[0-9+\\-\\s()]+$", message = "El formato del teléfono no es válido")
    String phone
) {}
