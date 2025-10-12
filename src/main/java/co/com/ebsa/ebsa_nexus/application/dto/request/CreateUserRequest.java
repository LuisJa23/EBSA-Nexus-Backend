package co.com.ebsa.ebsa_nexus.application.dto.request;

import co.com.ebsa.ebsa_nexus.domain.entity.User.WorkType;
import jakarta.validation.constraints.*;

/**
 * DTO para la creación de nuevos usuarios.
 * Contiene todas las validaciones necesarias para garantizar datos consistentes.
 */
public record CreateUserRequest(
    @NotBlank(message = "El username es obligatorio")
    @Size(min = 3, max = 45, message = "El username debe tener entre 3 y 45 caracteres")
    String username,
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe proporcionar un email válido")
    @Size(max = 60, message = "El email no puede exceder 60 caracteres")
    String email,
    
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, max = 20, message = "La contraseña debe tener entre 6 y 20 caracteres")
    String password,
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 45, message = "El nombre no puede exceder 45 caracteres")
    String firstName,
    
    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 45, message = "El apellido no puede exceder 45 caracteres")
    String lastName,
    
    @NotNull(message = "El ID del rol es obligatorio")
    Integer roleId,
    
    Integer workRoleId,
    
    WorkType workType,
    
    @Size(max = 45, message = "El número de documento no puede exceder 45 caracteres")
    String documentNumber,
    
    @NotBlank(message = "El teléfono es obligatorio")
    @Size(max = 45, message = "El teléfono no puede exceder 45 caracteres")
    String phone
) {}