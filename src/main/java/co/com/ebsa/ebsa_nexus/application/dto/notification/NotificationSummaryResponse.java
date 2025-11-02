package co.com.ebsa.ebsa_nexus.application.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSummaryResponse {
    private List<NotificationResponse> allNotifications;
    private long unreadCount;
    private List<NotificationResponse> recentNotifications;
}
