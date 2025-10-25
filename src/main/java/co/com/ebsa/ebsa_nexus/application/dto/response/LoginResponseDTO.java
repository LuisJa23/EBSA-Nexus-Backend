package co.com.ebsa.ebsa_nexus.application.dto.response;


public record LoginResponseDTO(
    Long id,
    String token, 
    String email, 
    String username, 
    String role,
    String workRole,
    String workType
) {}
