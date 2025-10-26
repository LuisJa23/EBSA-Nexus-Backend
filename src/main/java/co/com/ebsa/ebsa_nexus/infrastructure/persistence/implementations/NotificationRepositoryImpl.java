package co.com.ebsa.ebsa_nexus.infrastructure.persistence.implementations;

import co.com.ebsa.ebsa_nexus.domain.entity.Notification;
import co.com.ebsa.ebsa_nexus.domain.repository.NotificationRepository;
import co.com.ebsa.ebsa_nexus.infrastructure.persistence.jpa.repositories.JpaNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of NotificationRepository using JPA
 */
@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepository {
    
    private final JpaNotificationRepository jpaNotificationRepository;
    
    @Override
    public Notification save(Notification notification) {
        return jpaNotificationRepository.save(notification);
    }
    
    @Override
    public List<Notification> saveAll(List<Notification> notifications) {
        return jpaNotificationRepository.saveAll(notifications);
    }
    
    @Override
    public Optional<Notification> findById(Long id) {
        return jpaNotificationRepository.findById(id);
    }
    
    @Override
    public Page<Notification> findByUserId(Long userId, Pageable pageable) {
        return jpaNotificationRepository.findByUserId(userId, pageable);
    }
    
    @Override
    public List<Notification> findByUserId(Long userId) {
        return jpaNotificationRepository.findAllByUserId(userId);
    }
    
    @Override
    public List<Notification> findUnreadByUserId(Long userId) {
        return jpaNotificationRepository.findUnreadByUserId(userId);
    }
    
    @Override
    public Page<Notification> findByUserIdAndIsRead(Long userId, Boolean isRead, Pageable pageable) {
        return jpaNotificationRepository.findByUserIdAndIsRead(userId, isRead, pageable);
    }
    
    @Override
    public List<Notification> findByNoveltyId(Long noveltyId) {
        return jpaNotificationRepository.findByNoveltyId(noveltyId);
    }
    
    @Override
    public List<Notification> findByUserIdAndType(Long userId, String type) {
        return jpaNotificationRepository.findByUserIdAndType(userId, type);
    }
    
    @Override
    public List<Notification> findByUserIdAndCreatedAfter(Long userId, LocalDateTime date) {
        return jpaNotificationRepository.findByUserIdAndCreatedAfter(userId, date);
    }
    
    @Override
    public long countUnreadByUserId(Long userId) {
        return jpaNotificationRepository.countUnreadByUserId(userId);
    }
    
    @Override
    @Transactional
    public Notification markAsRead(Long notificationId) {
        Notification notification = jpaNotificationRepository.findById(notificationId)
            .orElseThrow(() -> new IllegalArgumentException("Notification not found with id: " + notificationId));
        
        notification.markAsRead();
        return jpaNotificationRepository.save(notification);
    }
    
    @Override
    @Transactional
    public void markAllAsReadByUserId(Long userId) {
        jpaNotificationRepository.markAllAsReadByUserId(userId);
    }
    
    @Override
    public void deleteById(Long id) {
        jpaNotificationRepository.deleteById(id);
    }
    
    @Override
    @Transactional
    public void deleteByUserId(Long userId) {
        jpaNotificationRepository.deleteByUserId(userId);
    }
    
    @Override
    public boolean existsById(Long id) {
        return jpaNotificationRepository.existsById(id);
    }
}
