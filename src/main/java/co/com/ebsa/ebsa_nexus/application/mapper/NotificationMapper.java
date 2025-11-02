package co.com.ebsa.ebsa_nexus.application.mapper;

import co.com.ebsa.ebsa_nexus.application.dto.notification.NotificationResponse;
import co.com.ebsa.ebsa_nexus.application.dto.notification.NotificationSummaryResponse;
import co.com.ebsa.ebsa_nexus.domain.entity.Notification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class NotificationMapper {
    
    public NotificationResponse toResponse(Notification notification) {
        if (notification == null) {
            return null;
        }
        
        return NotificationResponse.builder()
            .id(notification.getId())
            .userId(notification.getUser() != null ? notification.getUser().getId() : null)
            .noveltyId(notification.getNovelty() != null ? notification.getNovelty().getId() : null)
            .type(notification.getType())
            .title(notification.getTitle())
            .message(notification.getMessage())
            .isRead(notification.getIsRead())
            .createdAt(notification.getCreatedAt())
            .build();
    }
    
    public List<NotificationResponse> toResponseList(List<Notification> notifications) {
        return notifications.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
    
    public NotificationSummaryResponse toSummaryResponse(List<Notification> allNotifications, 
                                                         long unreadCount, 
                                                         List<Notification> recentNotifications) {
        return NotificationSummaryResponse.builder()
            .allNotifications(toResponseList(allNotifications))
            .unreadCount(unreadCount)
            .recentNotifications(toResponseList(recentNotifications))
            .build();
    }
}
