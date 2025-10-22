package co.com.ebsa.ebsa_nexus.infrastructure.persistence.jpa.repositories;

import co.com.ebsa.ebsa_nexus.domain.entity.NoveltyImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio Spring Data JPA para NoveltyImage.
 * 
 * @author EBSA Nexus Team
 * @version 1.0
 * @since 2025-10-21
 */
@Repository
public interface JpaNoveltyImageRepository extends JpaRepository<NoveltyImage, Long> {
    
    List<NoveltyImage> findByNoveltyIdOrderByUploadedAtDesc(Long noveltyId);
    
    long countByNoveltyId(Long noveltyId);
}
