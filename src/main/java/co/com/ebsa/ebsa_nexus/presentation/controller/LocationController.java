package co.com.ebsa.ebsa_nexus.presentation.controller;

import co.com.ebsa.ebsa_nexus.domain.entity.Location;
import co.com.ebsa.ebsa_nexus.domain.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for Location endpoints.
 * Provides API to fetch available municipalities.
 * 
 * @author EBSA Nexus Team
 * @version 1.0
 * @since 2025-11-02
 */
@Slf4j
@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {
    
    private final LocationRepository locationRepository;
    
    /**
     * Get all available locations/municipalities.
     * 
     * @return List of locations ordered by name
     */
    @GetMapping
    public ResponseEntity<List<Location>> getAllLocations() {
        log.info("Fetching all locations");
        List<Location> locations = locationRepository.findAllByOrderByNameAsc();
        return ResponseEntity.ok(locations);
    }
}
