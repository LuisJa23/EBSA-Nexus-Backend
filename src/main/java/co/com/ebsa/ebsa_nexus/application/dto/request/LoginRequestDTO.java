package co.com.ebsa.ebsa_nexus.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// LoginRequest.java
public record LoginRequestDTO(
  @NotBlank
  @Email(message = "Debe proporcionar un email válido")
  String email, 
  @NotBlank
  String password) {}
