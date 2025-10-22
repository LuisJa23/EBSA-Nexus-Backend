package co.com.ebsa.ebsa_nexus.domain.repository;

import co.com.ebsa.ebsa_nexus.domain.entity.NoveltyImage;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de dominio para la entidad NoveltyImage.
 * 
 * @author EBSA Nexus Team
 * @version 1.0
 * @since 2025-10-21
 */
public interface NoveltyImageRepository {
    
    /**
     * Guarda una imagen de novedad.
     * 
     * @param image Imagen a guardar
     * @return Imagen guardada con ID asignado
     * @throws IllegalArgumentException si image es null
     */
    NoveltyImage save(NoveltyImage image);
    
    /**
     * Busca una imagen por su ID.
     * 
     * @param id ID de la imagen
     * @return Optional con la imagen si existe
     */
    Optional<NoveltyImage> findById(Long id);
    
    /**
     * Obtiene todas las imágenes de una novedad específica.
     * 
     * @param noveltyId ID de la novedad
     * @return Lista de imágenes de la novedad
     */
    List<NoveltyImage> findByNoveltyId(Long noveltyId);
    
    /**
     * Cuenta el número de imágenes de una novedad.
     * 
     * @param noveltyId ID de la novedad
     * @return Número de imágenes
     */
    long countByNoveltyId(Long noveltyId);
    
    /**
     * Obtiene todas las imágenes de una novedad ordenadas por fecha de carga.
     * 
     * @param noveltyId ID de la novedad
     * @return Lista de imágenes ordenadas
     */
    List<NoveltyImage> findByNoveltyIdOrderByUploadedAtDesc(Long noveltyId);
    
    /**
     * Guarda una lista de imágenes.
     * 
     * @param images Lista de imágenes a guardar
     * @return Lista de imágenes guardadas
     */
    List<NoveltyImage> saveAll(List<NoveltyImage> images);
    
    /**
     * Elimina una imagen.
     * 
     * @param id ID de la imagen a eliminar
     */
    void deleteById(Long id);
    
    /**
     * Elimina todas las imágenes de una novedad.
     * 
     * @param noveltyId ID de la novedad
     */
    void deleteByNoveltyId(Long noveltyId);
    
    /**
     * Verifica si existe una imagen con el ID dado.
     * 
     * @param id ID de la imagen
     * @return true si existe, false en caso contrario
     */
    boolean existsById(Long id);
}
