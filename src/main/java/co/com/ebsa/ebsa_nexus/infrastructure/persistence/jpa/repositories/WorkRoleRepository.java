package co.com.ebsa.ebsa_nexus.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.com.ebsa.ebsa_nexus.domain.entity.WorkRole;

import java.util.List;
import java.util.Optional;

/**
 * WorkRoleRepository usando Spring Data JPA
 */
@Repository
public interface WorkRoleRepository extends JpaRepository<WorkRole, Long> {

    Optional<WorkRole> findByName(String name);
    
    List<WorkRole> findByTypeOrderByName(WorkRole.WorkRoleType type);
    
    List<WorkRole> findAllByOrderByTypeAscNameAsc();
    
    boolean existsByName(String name);
}