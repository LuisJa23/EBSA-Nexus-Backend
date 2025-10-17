package co.com.ebsa.ebsa_nexus.presentation.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.com.ebsa.ebsa_nexus.application.dto.request.auth.LoginRequestDTO;
import co.com.ebsa.ebsa_nexus.application.dto.response.LoginResponseDTO;
import co.com.ebsa.ebsa_nexus.application.service.AuthenticateApplicationService;

@RestController
@RequestMapping("/auth")
public class AuthController {
  
  private final AuthenticateApplicationService authenticateService;

    public AuthController(AuthenticateApplicationService authenticateService) {
        this.authenticateService = authenticateService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        LoginResponseDTO response = authenticateService.login(request);
        return ResponseEntity.ok(response);
    }
}
