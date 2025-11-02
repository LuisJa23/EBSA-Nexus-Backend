package co.com.ebsa.ebsa_nexus.domain.repository;

import co.com.ebsa.ebsa_nexus.domain.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de dominio para la entidad Notification.
 * 
 * @author EBSA Nexus Team
 * @version 1.0
 * @since 2025-10-21
 */
public interface NotificationRepository {
    
    /**
     * Guarda una notificación.
     * 
     * @param notification Notificación a guardar
     * @return Notificación guardada con ID asignado
     * @throws IllegalArgumentException si notification es null
     */
    Notification save(Notification notification);
    
    /**
     * Guarda múltiples notificaciones.
     * 
     * @param notifications Lista de notificaciones a guardar
     * @return Lista de notificaciones guardadas
     */
    List<Notification> saveAll(List<Notification> notifications);
    
    /**
     * Busca una notificación por su ID.
     * 
     * @param id ID de la notificación
     * @return Optional con la notificación si existe
     */
    Optional<Notification> findById(Long id);
    
    /**
     * Obtiene todas las notificaciones de un usuario con paginación.
     * 
     * @param userId ID del usuario
     * @param pageable Configuración de paginación
     * @return Página de notificaciones
     */
    Page<Notification> findByUserId(Long userId, Pageable pageable);
    
    /**
     * Obtiene notificaciones no leídas de un usuario.
     * 
     * @param userId ID del usuario
     * @return Lista de notificaciones no leídas
     */
    List<Notification> findUnreadByUserId(Long userId);
    
    /**
     * Obtiene notificaciones de un usuario filtradas por estado de lectura.
     * 
     * @param userId ID del usuario
     * @param isRead Estado de lectura
     * @param pageable Configuración de paginación
     * @return Página de notificaciones
     */
    Page<Notification> findByUserIdAndIsRead(Long userId, Boolean isRead, Pageable pageable);
    
    /**
     * Obtiene notificaciones relacionadas con una novedad específica.
     * 
     * @param noveltyId ID de la novedad
     * @return Lista de notificaciones
     */
    List<Notification> findByNoveltyId(Long noveltyId);
    
    /**
     * Obtiene todas las notificaciones de un usuario sin paginación.
     * 
     * @param userId ID del usuario
     * @return Lista de todas las notificaciones del usuario
     */
    List<Notification> findByUserId(Long userId);
    
    /**
     * Obtiene notificaciones de un usuario por tipo.
     * 
     * @param userId ID del usuario
     * @param type Tipo de notificación
     * @return Lista de notificaciones del tipo especificado
     */
    List<Notification> findByUserIdAndType(Long userId, String type);
    
    /**
     * Obtiene notificaciones de un usuario creadas después de una fecha.
     * 
     * @param userId ID del usuario
     * @param date Fecha de referencia
     * @return Lista de notificaciones creadas después de la fecha
     */
    List<Notification> findByUserIdAndCreatedAfter(Long userId, java.time.LocalDateTime date);
    
    /**
     * Marca una notificación específica como leída.
     * 
     * @param notificationId ID de la notificación
     * @return Notificación actualizada
     */
    Notification markAsRead(Long notificationId);
    
    /**
     * Cuenta notificaciones no leídas de un usuario.
     * 
     * @param userId ID del usuario
     * @return Número de notificaciones no leídas
     */
    long countUnreadByUserId(Long userId);
    
    /**
     * Marca todas las notificaciones de un usuario como leídas.
     * 
     * @param userId ID del usuario
     */
    void markAllAsReadByUserId(Long userId);
    
    /**
     * Elimina una notificación.
     * 
     * @param id ID de la notificación
     */
    void deleteById(Long id);
    
    /**
     * Elimina todas las notificaciones de un usuario.
     * 
     * @param userId ID del usuario
     */
    void deleteByUserId(Long userId);
    
    /**
     * Verifica si existe una notificación con el ID dado.
     * 
     * @param id ID de la notificación
     * @return true si existe, false en caso contrario
     */
    boolean existsById(Long id);
}
