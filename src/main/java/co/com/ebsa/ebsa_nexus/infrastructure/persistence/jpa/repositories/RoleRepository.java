package co.com.ebsa.ebsa_nexus.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.com.ebsa.ebsa_nexus.domain.entity.Role;

import java.util.Optional;

/**
 * RoleRepository usando Spring Data JPA
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    Optional<Role> findByName(String name);
    boolean existsByName(String name);
}