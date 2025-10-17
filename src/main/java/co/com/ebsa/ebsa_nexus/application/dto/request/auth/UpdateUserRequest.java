package co.com.ebsa.ebsa_nexus.application.dto.request.auth;

import co.com.ebsa.ebsa_nexus.domain.entity.User.WorkType;
import jakarta.validation.constraints.*;

/**
 * DTO para la actualización de usuarios existentes.
 * Todos los campos son opcionales para permitir actualizaciones parciales.
 */
public record UpdateUserRequest(
    @Size(min = 3, max = 45, message = "El username debe tener entre 3 y 45 caracteres")
    String username,
    
    @Email(message = "Debe proporcionar un email válido")
    @Size(max = 60, message = "El email no puede exceder 60 caracteres")
    String email,
    
    @Size(min = 6, max = 20, message = "La contraseña debe tener entre 6 y 20 caracteres")
    String password,
    
    @Size(max = 45, message = "El nombre no puede exceder 45 caracteres")
    String firstName,
    
    @Size(max = 45, message = "El apellido no puede exceder 45 caracteres")
    String lastName,
    
    Long roleId,
    Long workRoleId,
    WorkType workType,
    
    @Size(max = 45, message = "El número de documento no puede exceder 45 caracteres")
    String documentNumber,
    
    @Size(max = 45, message = "El teléfono no puede exceder 45 caracteres")
    String phone,
    
    Boolean active
) {}