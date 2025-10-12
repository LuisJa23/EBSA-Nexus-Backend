package co.com.ebsa.ebsa_nexus.application.dto.response;

import co.com.ebsa.ebsa_nexus.domain.entity.User.WorkType;
import java.time.LocalDateTime;

/**
 * DTO de respuesta que representa un usuario del sistema.
 * Incluye información completa del usuario sin exponer datos sensibles como contraseñas.
 */
public record UserResponse(
    Integer id,
    String uuid,
    String username,
    String email,
    String firstName,
    String lastName,
    String roleName,
    String workRoleName,
    WorkType workType,
    String documentNumber,
    String phone,
    Boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime lastLogin
) {}