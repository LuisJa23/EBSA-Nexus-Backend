package co.com.ebsa.ebsa_nexus.application.service;

import co.com.ebsa.ebsa_nexus.domain.entity.Notification;
import co.com.ebsa.ebsa_nexus.domain.entity.User;
import co.com.ebsa.ebsa_nexus.domain.enums.NotificationType;
import co.com.ebsa.ebsa_nexus.domain.repository.NotificationRepository;
import co.com.ebsa.ebsa_nexus.domain.repository.UserDomainRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for managing notifications
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationApplicationService {
    
    private final NotificationRepository notificationRepository;
    private final UserDomainRepository userRepository;
    
    /**
     * Create a new notification
     */
    public Notification createNotification(Long userId, String type, 
                                          String title, String message, Long noveltyId) {
        log.info("Creating notification for user: {} with type: {}", userId, type);
        
        // Validate user exists
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        
        // Build notification
        Notification notification = Notification.builder()
            .user(user)
            .novelty(null) // Set later if needed
            .type(type)
            .title(title)
            .message(message)
            .isRead(false)
            .createdAt(LocalDateTime.now())
            .build();
        
        // Validate notification
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Notification title cannot be empty");
        }
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Notification message cannot be empty");
        }
        if (title.length() > 200) {
            throw new IllegalArgumentException("Notification title cannot exceed 200 characters");
        }
        
        // Save notification
        Notification saved = notificationRepository.save(notification);
        log.info("Notification created successfully with id: {}", saved.getId());
        
        return saved;
    }
    
    /**
     * Get all notifications for a user
     */
    @Transactional(readOnly = true)
    public List<Notification> getUserNotifications(Long userId) {
        log.info("Retrieving all notifications for user: {}", userId);
        return notificationRepository.findByUserId(userId);
    }
    
    /**
     * Get unread notifications for a user
     */
    @Transactional(readOnly = true)
    public List<Notification> getUnreadNotifications(Long userId) {
        log.info("Retrieving unread notifications for user: {}", userId);
        return notificationRepository.findUnreadByUserId(userId);
    }
    
    /**
     * Get notifications by type for a user
     */
    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByType(Long userId, String type) {
        log.info("Retrieving notifications of type {} for user: {}", type, userId);
        return notificationRepository.findByUserIdAndType(userId, type);
    }
    
    /**
     * Get notifications for a novelty
     */
    @Transactional(readOnly = true)
    public List<Notification> getNoveltyNotifications(Long noveltyId) {
        log.info("Retrieving notifications for novelty: {}", noveltyId);
        return notificationRepository.findByNoveltyId(noveltyId);
    }
    
    /**
     * Get recent notifications (last 7 days)
     */
    @Transactional(readOnly = true)
    public List<Notification> getRecentNotifications(Long userId) {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        log.info("Retrieving recent notifications for user: {} since {}", userId, sevenDaysAgo);
        return notificationRepository.findByUserIdAndCreatedAfter(userId, sevenDaysAgo);
    }
    
    /**
     * Count unread notifications
     */
    @Transactional(readOnly = true)
    public long countUnreadNotifications(Long userId) {
        log.info("Counting unread notifications for user: {}", userId);
        return notificationRepository.countUnreadByUserId(userId);
    }
    
    /**
     * Mark a notification as read
     */
    public Notification markAsRead(Long notificationId) {
        log.info("Marking notification as read: {}", notificationId);
        
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new IllegalArgumentException("Notification not found with id: " + notificationId));
        
        if (Boolean.TRUE.equals(notification.getIsRead())) {
            log.info("Notification {} is already read", notificationId);
            return notification;
        }
        
        return notificationRepository.markAsRead(notificationId);
    }
    
    /**
     * Mark all notifications as read for a user
     */
    public void markAllAsRead(Long userId) {
        log.info("Marking all notifications as read for user: {}", userId);
        notificationRepository.markAllAsReadByUserId(userId);
    }
    
    /**
     * Delete a notification
     */
    public void deleteNotification(Long notificationId) {
        log.info("Deleting notification: {}", notificationId);
        notificationRepository.deleteById(notificationId);
    }
    
    /**
     * Delete all notifications for a user
     */
    public void deleteAllUserNotifications(Long userId) {
        log.info("Deleting all notifications for user: {}", userId);
        notificationRepository.deleteByUserId(userId);
    }
}
