package co.com.ebsa.ebsa_nexus.domain.repository;

import co.com.ebsa.ebsa_nexus.domain.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Location entity.
 * Provides data access for municipalities.
 * 
 * @author EBSA Nexus Team
 * @version 1.0
 * @since 2025-11-02
 */
@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    
    /**
     * Find all locations ordered by name.
     */
    List<Location> findAllByOrderByNameAsc();
    
    /**
     * Find location by name (case insensitive).
     */
    Location findByNameIgnoreCase(String name);
}
