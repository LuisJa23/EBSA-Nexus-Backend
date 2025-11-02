package co.com.ebsa.ebsa_nexus.presentation.controller;

import co.com.ebsa.ebsa_nexus.application.dto.notification.CreateNotificationRequest;
import co.com.ebsa.ebsa_nexus.application.dto.notification.NotificationResponse;
import co.com.ebsa.ebsa_nexus.application.dto.notification.NotificationSummaryResponse;
import co.com.ebsa.ebsa_nexus.application.mapper.NotificationMapper;
import co.com.ebsa.ebsa_nexus.application.service.NotificationApplicationService;
import co.com.ebsa.ebsa_nexus.domain.entity.Notification;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing notifications
 */
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    
    private final NotificationApplicationService notificationService;
    private final NotificationMapper notificationMapper;
    
    /**
     * Create a new notification
     * POST /api/v1/notifications
     */
    @PostMapping
    public ResponseEntity<NotificationResponse> createNotification(
            @Valid @RequestBody CreateNotificationRequest request) {
        
        log.info("Creating notification for user: {}", request.getUserId());
        
        Notification notification = notificationService.createNotification(
            request.getUserId(),
            request.getType(),
            request.getTitle(),
            request.getMessage(),
            request.getNoveltyId()
        );
        
        NotificationResponse response = notificationMapper.toResponse(notification);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Get all notifications for a user (called on login)
     * GET /api/v1/notifications/user/{userId}/summary
     */
    @GetMapping("/user/{userId}/summary")
    public ResponseEntity<NotificationSummaryResponse> getUserNotificationsSummary(
            @PathVariable Long userId) {
        
        log.info("Loading notification summary for user: {}", userId);
        
        List<Notification> allNotifications = notificationService.getUserNotifications(userId);
        long unreadCount = notificationService.countUnreadNotifications(userId);
        List<Notification> recentNotifications = notificationService.getRecentNotifications(userId);
        
        NotificationSummaryResponse response = notificationMapper.toSummaryResponse(
            allNotifications, unreadCount, recentNotifications
        );
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get all notifications for a user
     * GET /api/v1/notifications/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponse>> getUserNotifications(
            @PathVariable Long userId) {
        
        log.info("Retrieving all notifications for user: {}", userId);
        
        List<Notification> notifications = notificationService.getUserNotifications(userId);
        List<NotificationResponse> response = notificationMapper.toResponseList(notifications);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get unread notifications for a user
     * GET /api/v1/notifications/user/{userId}/unread
     */
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotifications(
            @PathVariable Long userId) {
        
        log.info("Retrieving unread notifications for user: {}", userId);
        
        List<Notification> notifications = notificationService.getUnreadNotifications(userId);
        List<NotificationResponse> response = notificationMapper.toResponseList(notifications);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get unread notification count
     * GET /api/v1/notifications/user/{userId}/unread/count
     */
    @GetMapping("/user/{userId}/unread/count")
    public ResponseEntity<Long> getUnreadCount(@PathVariable Long userId) {
        log.info("Counting unread notifications for user: {}", userId);
        long count = notificationService.countUnreadNotifications(userId);
        return ResponseEntity.ok(count);
    }
    
    /**
     * Get notifications by type
     * GET /api/v1/notifications/user/{userId}/type/{type}
     */
    @GetMapping("/user/{userId}/type/{type}")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByType(
            @PathVariable Long userId,
            @PathVariable String type) {
        
        log.info("Retrieving notifications of type {} for user: {}", type, userId);
        
        List<Notification> notifications = notificationService.getNotificationsByType(userId, type);
        List<NotificationResponse> response = notificationMapper.toResponseList(notifications);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get notifications for a novelty
     * GET /api/v1/notifications/novelty/{noveltyId}
     */
    @GetMapping("/novelty/{noveltyId}")
    public ResponseEntity<List<NotificationResponse>> getNoveltyNotifications(
            @PathVariable Long noveltyId) {
        
        log.info("Retrieving notifications for novelty: {}", noveltyId);
        
        List<Notification> notifications = notificationService.getNoveltyNotifications(noveltyId);
        List<NotificationResponse> response = notificationMapper.toResponseList(notifications);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Mark a notification as read
     * PATCH /api/v1/notifications/{notificationId}/read
     */
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable Long notificationId) {
        log.info("Marking notification as read: {}", notificationId);
        
        Notification notification = notificationService.markAsRead(notificationId);
        NotificationResponse response = notificationMapper.toResponse(notification);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Mark all notifications as read for a user
     * PATCH /api/v1/notifications/user/{userId}/read-all
     */
    @PatchMapping("/user/{userId}/read-all")
    public ResponseEntity<Void> markAllAsRead(@PathVariable Long userId) {
        log.info("Marking all notifications as read for user: {}", userId);
        notificationService.markAllAsRead(userId);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Delete a notification
     * DELETE /api/v1/notifications/{notificationId}
     */
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long notificationId) {
        log.info("Deleting notification: {}", notificationId);
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Delete all notifications for a user
     * DELETE /api/v1/notifications/user/{userId}
     */
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> deleteAllUserNotifications(@PathVariable Long userId) {
        log.info("Deleting all notifications for user: {}", userId);
        notificationService.deleteAllUserNotifications(userId);
        return ResponseEntity.noContent().build();
    }
}
