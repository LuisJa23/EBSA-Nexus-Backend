package co.com.ebsa.ebsa_nexus.infrastructure.persistence.adapter;

import co.com.ebsa.ebsa_nexus.domain.entity.NoveltyImage;
import co.com.ebsa.ebsa_nexus.domain.repository.NoveltyImageRepository;
import co.com.ebsa.ebsa_nexus.infrastructure.persistence.jpa.repositories.JpaNoveltyImageRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador que implementa NoveltyImageRepository del dominio usando JPA.
 * 
 * @author EBSA Nexus Team
 * @version 1.0
 * @since 2025-10-21
 */
@Component
public class NoveltyImageRepositoryAdapter implements NoveltyImageRepository {

    private final JpaNoveltyImageRepository jpaNoveltyImageRepository;

    public NoveltyImageRepositoryAdapter(JpaNoveltyImageRepository jpaNoveltyImageRepository) {
        this.jpaNoveltyImageRepository = jpaNoveltyImageRepository;
    }

    @Override
    public NoveltyImage save(NoveltyImage image) {
        return jpaNoveltyImageRepository.save(image);
    }

    @Override
    public Optional<NoveltyImage> findById(Long id) {
        return jpaNoveltyImageRepository.findById(id);
    }

    @Override
    public List<NoveltyImage> findByNoveltyId(Long noveltyId) {
        return jpaNoveltyImageRepository.findByNoveltyIdOrderByUploadedAtDesc(noveltyId);
    }

    @Override
    public long countByNoveltyId(Long noveltyId) {
        return jpaNoveltyImageRepository.countByNoveltyId(noveltyId);
    }

    @Override
    public List<NoveltyImage> findByNoveltyIdOrderByUploadedAtDesc(Long noveltyId) {
        return jpaNoveltyImageRepository.findByNoveltyIdOrderByUploadedAtDesc(noveltyId);
    }

    @Override
    public List<NoveltyImage> saveAll(List<NoveltyImage> images) {
        return jpaNoveltyImageRepository.saveAll(images);
    }

    @Override
    public void deleteById(Long id) {
        jpaNoveltyImageRepository.deleteById(id);
    }
}
