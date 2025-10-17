package co.com.ebsa.ebsa_nexus.presentation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import co.com.ebsa.ebsa_nexus.domain.entity.Area;
import co.com.ebsa.ebsa_nexus.infrastructure.repository.AreaRepository;

@RestController
@RequestMapping("/api/areas")
public class AreaController {

    @Autowired
    private AreaRepository areaRepository;

    @GetMapping
    public List<Area> getAllAreas() {
        return areaRepository.findAll();
    }

}
