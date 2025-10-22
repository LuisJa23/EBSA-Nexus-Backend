package co.com.ebsa.ebsa_nexus.application.service.novelty;

import co.com.ebsa.ebsa_nexus.domain.entity.Notification;
import co.com.ebsa.ebsa_nexus.domain.entity.Novelty;
import co.com.ebsa.ebsa_nexus.domain.entity.NoveltyAssignment;
import co.com.ebsa.ebsa_nexus.domain.entity.User;
import co.com.ebsa.ebsa_nexus.domain.repository.NotificationRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service to handle notifications for novelty events
 */
@Service
@Transactional
public class NoveltyNotificationService {

    private final NotificationRepository notificationRepository;
    private final EntityManager entityManager;

    public NoveltyNotificationService(NotificationRepository notificationRepository, EntityManager entityManager) {
        this.notificationRepository = notificationRepository;
        this.entityManager = entityManager;
    }

    /**
     * Notify admin when a new novelty is reported
     */
    public void notifyNewNovelty(Novelty novelty) {
        // TODO: Get all admin users when User entity is available
        // List<User> admins = userRepository.findByRole("ADMIN");
        // for (User admin : admins) {
        //     createNotification(admin.getId(), novelty, "NEW_NOVELTY", 
        //         "Nueva novedad reportada", 
        //         "Se ha reportado una nueva novedad que requiere asignación");
        // }
        
        // For now, create a generic notification
        createNotification(null, novelty, "NEW_NOVELTY", 
            "Nueva novedad reportada", 
            "Se ha reportado una nueva novedad por el supervisor. Requiere asignación de cuadrilla.");
    }

    /**
     * Notify crew when assigned to a novelty
     */
    public void notifyCrewAssignment(Novelty novelty, NoveltyAssignment assignment) {
        // TODO: Get crew members when Crew-User relationship is available
        // List<User> crewMembers = userRepository.findByCrewId(assignment.getAssignedCrewId());
        // for (User member : crewMembers) {
        //     createNotification(member.getId(), novelty, "CREW_ASSIGNED",
        //         "Cuadrilla asignada a novedad",
        //         "Su cuadrilla ha sido asignada para resolver una novedad");
        // }
        
        // For now, create a generic notification
        createNotification(null, novelty, "CREW_ASSIGNED",
            "Cuadrilla asignada a novedad",
            String.format("La cuadrilla ID %d ha sido asignada. Prioridad: %s. Instrucciones: %s",
                assignment.getAssignedCrewId(),
                assignment.getPriority(),
                assignment.getInstructions()));
    }

    /**
     * Notify when novelty status changes
     */
    public void notifyStatusChange(Novelty novelty) {
        // Notify supervisor who reported the novelty
        createNotification(novelty.getReportedByUserId(), novelty, "STATUS_CHANGE",
            "Cambio de estado en novedad",
            String.format("La novedad ha cambiado de estado a: %s", novelty.getStatus()));

        // TODO: Also notify admin users
    }

    /**
     * Notify when novelty is resolved
     */
    public void notifyResolution(Novelty novelty) {
        // Notify supervisor who reported the novelty
        createNotification(novelty.getReportedByUserId(), novelty, "NOVELTY_RESOLVED",
            "Novedad resuelta",
            "La novedad ha sido marcada como resuelta. Pendiente de verificación administrativa.");

        // TODO: Notify all admin users for verification
        // List<User> admins = userRepository.findByRole("ADMIN");
        // for (User admin : admins) {
        //     createNotification(admin.getId(), novelty, "PENDING_VERIFICATION",
        //         "Novedad pendiente de verificación",
        //         "Una novedad ha sido resuelta y requiere verificación");
        // }
    }

    /**
     * Notify when resolution is rejected
     */
    public void notifyRejection(Novelty novelty) {
        // TODO: Notify crew members of the assigned crew
        // NoveltyAssignment assignment = assignmentRepository.findByNoveltyId(novelty.getId());
        // List<User> crewMembers = userRepository.findByCrewId(assignment.getAssignedCrewId());
        // for (User member : crewMembers) {
        //     createNotification(member.getId(), novelty, "RESOLUTION_REJECTED",
        //         "Resolución rechazada",
        //         "La resolución de la novedad ha sido rechazada. Requiere trabajo adicional");
        // }
        
        createNotification(novelty.getResolvedByUserId(), novelty, "RESOLUTION_REJECTED",
            "Resolución rechazada",
            String.format("La resolución ha sido rechazada. Motivo: %s", novelty.getVerificationNotes()));
    }

    /**
     * Notify when novelty is cancelled
     */
    public void notifyCancellation(Novelty novelty) {
        // Notify supervisor who reported
        createNotification(novelty.getReportedByUserId(), novelty, "NOVELTY_CANCELLED",
            "Novedad cancelada",
            String.format("La novedad ha sido cancelada. Motivo: %s", novelty.getCancellationReason()));

        // TODO: Notify assigned crew if exists
    }

    /**
     * Notify about overdue novelties (called by scheduled job)
     */
    public void notifyOverdue(Novelty novelty, NoveltyAssignment assignment) {
        // TODO: Notify admin and assigned crew
        createNotification(null, novelty, "NOVELTY_OVERDUE",
            "Novedad vencida",
            String.format("La novedad ha superado la fecha estimada de resolución: %s",
                assignment.getEstimatedResolutionDate()));
    }

    // Private helper method to create notification
    private void createNotification(Long userId, Novelty novelty, String type, String title, String message) {
        // Skip notification if userId is null (for now, until we implement admin user lookup)
        if (userId == null) {
            return;
        }
        
        Notification notification = new Notification();
        
        // Use EntityManager.getReference to create a lazy proxy without hitting the database
        // This assumes the userId exists in the database
        User userProxy = entityManager.getReference(User.class, userId);
        notification.setUser(userProxy);
        
        // Set novelty directly
        notification.setNovelty(novelty);
        
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        
        notificationRepository.save(notification);
    }
}
