package co.com.ebsa.ebsa_nexus.presentation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import co.com.ebsa.ebsa_nexus.domain.entity.WorkRole;
import co.com.ebsa.ebsa_nexus.infrastructure.repository.WorkRoleRepository;

@RestController
@RequestMapping("/api/work-roles")
public class WorkRoleController {

    @Autowired
    private WorkRoleRepository workRoleRepository;

    @GetMapping
    public List<WorkRole> getAllWorkRoles() {
        return workRoleRepository.findAllByOrderByTypeAscNameAsc();
    }

    @GetMapping("/type/{type}")
    public List<WorkRole> getWorkRolesByType(@PathVariable WorkRole.WorkRoleType type) {
        return workRoleRepository.findByTypeOrderByName(type);
    }
}
