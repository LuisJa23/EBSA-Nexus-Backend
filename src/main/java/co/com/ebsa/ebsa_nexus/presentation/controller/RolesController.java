package co.com.ebsa.ebsa_nexus.presentation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import co.com.ebsa.ebsa_nexus.domain.entity.Role;
import co.com.ebsa.ebsa_nexus.infrastructure.repository.RoleRepository;


@RestController
@RequestMapping("/api/roles")
public class RolesController {

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
}