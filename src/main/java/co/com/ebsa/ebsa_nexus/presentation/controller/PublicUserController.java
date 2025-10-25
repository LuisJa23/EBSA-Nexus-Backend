package co.com.ebsa.ebsa_nexus.presentation.controller;


import co.com.ebsa.ebsa_nexus.application.dto.response.UserWorkerResponse;
import co.com.ebsa.ebsa_nexus.domain.entity.User;
import co.com.ebsa.ebsa_nexus.infrastructure.persistence.jpa.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/public/workers")
public class PublicUserController {
    
    private final UserRepository userRepository;
    
    public PublicUserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<?> getAllWorkers() {
        try {
            log.info("Fetching all workers from the system");
            List<User> users = userRepository.findAll();
            
            // Convertir a DTOs simples
            List<UserWorkerResponse> workers = users.stream()
                .map(user -> UserWorkerResponse.builder()
                    .id(user.getId())
                    .uuid(user.getUuid())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .workType(user.getWorkType().toString())
                    .documentNumber(user.getDocumentNumber())
                    .phone(user.getPhone())
                    .active(user.getActive() != null ? user.getActive() : false)
                    .build())
                .collect(Collectors.toList());
            
            log.info("Retrieved {} workers", workers.size());
            return ResponseEntity.ok(workers);
        } catch (Exception e) {
            log.error("Error fetching workers", e);
            return ResponseEntity.status(500).body("Error interno del servidor: " + e.getMessage());
        }
    }
}