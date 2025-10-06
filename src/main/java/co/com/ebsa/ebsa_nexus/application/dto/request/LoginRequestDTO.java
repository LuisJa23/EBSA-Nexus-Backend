package co.com.ebsa.ebsa_nexus.application.dto.request;

import jakarta.validation.constraints.NotBlank;

// LoginRequest.java
public record LoginRequestDTO(
  @NotBlank
  String username, 
  @NotBlank
  String password) {}
