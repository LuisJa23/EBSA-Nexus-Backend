package co.com.ebsa.ebsa_nexus.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.com.ebsa.ebsa_nexus.domain.entity.Area;

import java.util.Optional;

@Repository
public interface AreaRepository extends JpaRepository<Area, Integer> {
    Optional<Area> findByName(String name);
    boolean existsByName(String name);
}