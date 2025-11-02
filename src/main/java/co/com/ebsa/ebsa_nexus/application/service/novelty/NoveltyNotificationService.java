package co.com.ebsa.ebsa_nexus.application.service.novelty;

import co.com.ebsa.ebsa_nexus.domain.entity.CrewMember;
import co.com.ebsa.ebsa_nexus.domain.entity.Notification;
import co.com.ebsa.ebsa_nexus.domain.entity.Novelty;
import co.com.ebsa.ebsa_nexus.domain.entity.NoveltyAssignment;
import co.com.ebsa.ebsa_nexus.domain.entity.User;
import co.com.ebsa.ebsa_nexus.domain.repository.CrewMemberRepository;
import co.com.ebsa.ebsa_nexus.domain.repository.NotificationRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service to handle notifications for novelty events
 */
@Slf4j
@Service
@Transactional
public class NoveltyNotificationService {

    private final NotificationRepository notificationRepository;
    private final EntityManager entityManager;
    private final CrewMemberRepository crewMemberRepository;

    public NoveltyNotificationService(
            NotificationRepository notificationRepository, 
            EntityManager entityManager,
            CrewMemberRepository crewMemberRepository) {
        this.notificationRepository = notificationRepository;
        this.entityManager = entityManager;
        this.crewMemberRepository = crewMemberRepository;
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
        try {
            log.info("Iniciando notificación de asignación de novedad. NoveltyId: {}, CrewId: {}", 
                    novelty.getId(), assignment.getAssignedCrewId());
            
            // Obtener todos los miembros activos de la cuadrilla asignada
            List<CrewMember> crewMembers = crewMemberRepository.findActiveMembers(assignment.getAssignedCrewId());
            
            log.info("Se encontraron {} miembros activos en la cuadrilla {}", 
                    crewMembers.size(), assignment.getAssignedCrewId());
            
            if (crewMembers.isEmpty()) {
                log.warn("No se encontraron miembros activos en la cuadrilla {}. No se enviarán notificaciones.", 
                        assignment.getAssignedCrewId());
                return;
            }
            
            // Crear mensaje con prioridad e instrucciones
            String message = String.format("Su cuadrilla ha sido asignada para resolver una novedad. " +
                    "Prioridad: %s. %s",
                    assignment.getPriority() != null ? assignment.getPriority() : "NORMAL",
                    assignment.getInstructions() != null ? "Instrucciones: " + assignment.getInstructions() : "");
            
            // Enviar notificación a cada miembro activo de la cuadrilla
            int notificationsCreated = 0;
            for (CrewMember member : crewMembers) {
                try {
                    log.debug("Creando notificación para usuario {}", member.getUserId());
                    createNotification(
                        member.getUserId(), 
                        novelty, 
                        "CREW_ASSIGNED",
                        "Nueva asignación de novedad",
                        message
                    );
                    notificationsCreated++;
                } catch (Exception e) {
                    log.error("Error al crear notificación para usuario {}: {}", 
                            member.getUserId(), e.getMessage(), e);
                }
            }
            
            log.info("Se crearon {} notificaciones exitosamente de {} miembros", 
                    notificationsCreated, crewMembers.size());
            
        } catch (Exception e) {
            log.error("Error general al notificar asignación de cuadrilla: {}", e.getMessage(), e);
        }
    }

    /**
     * Notify when novelty status changes
     */
    public void notifyStatusChange(Novelty novelty) {
        // Notify user who created the novelty
        createNotification(novelty.getCreatedBy(), novelty, "STATUS_CHANGE",
            "Cambio de estado en novedad",
            String.format("La novedad ha cambiado de estado a: %s", novelty.getStatus()));

        // TODO: Also notify admin users
    }

    /**
     * Notify when novelty is completed
     */
    public void notifyResolution(Novelty novelty) {
        // Notify user who created the novelty
        createNotification(novelty.getCreatedBy(), novelty, "NOVELTY_COMPLETED",
            "Novedad completada",
            "La novedad ha sido marcada como completada. Pendiente de cierre administrativo.");

        // TODO: Notify all admin users for verification
        // List<User> admins = userRepository.findByRole("ADMIN");
        // for (User admin : admins) {
        //     createNotification(admin.getId(), novelty, "PENDING_CLOSURE",
        //         "Novedad pendiente de cierre",
        //         "Una novedad ha sido completada y requiere cierre administrativo");
        // }
    }

    /**
     * Notify when completion is rejected
     */
    public void notifyRejection(Novelty novelty) {
        // Si la novedad tiene una cuadrilla asignada, notificar a los miembros
        if (novelty.getCrewId() != null) {
            List<CrewMember> crewMembers = crewMemberRepository.findActiveMembers(novelty.getCrewId());
            
            for (CrewMember member : crewMembers) {
                createNotification(
                    member.getUserId(), 
                    novelty, 
                    "COMPLETION_REJECTED",
                    "Completación rechazada",
                    "La completación de la novedad ha sido rechazada. Requiere trabajo adicional. " +
                    "Por favor revise las observaciones y corrija los problemas identificados."
                );
            }
        }
    }

    /**
     * Notify when novelty is cancelled
     */
    public void notifyCancellation(Novelty novelty) {
        // Notify user who created the novelty
        createNotification(novelty.getCreatedBy(), novelty, "NOVELTY_CANCELLED",
            "Novedad cancelada",
            "La novedad ha sido cancelada. Revisa las observaciones para más detalles.");

        // Notify assigned crew if exists
        if (novelty.getCrewId() != null) {
            List<CrewMember> crewMembers = crewMemberRepository.findActiveMembers(novelty.getCrewId());
            
            for (CrewMember member : crewMembers) {
                createNotification(
                    member.getUserId(), 
                    novelty, 
                    "NOVELTY_CANCELLED",
                    "Novedad cancelada",
                    "La novedad asignada a su cuadrilla ha sido cancelada. No es necesario continuar trabajando en ella."
                );
            }
        }
    }

    /**
     * Notify about overdue novelties (called by scheduled job)
     */
    public void notifyOverdue(Novelty novelty, NoveltyAssignment assignment) {
        // Notify assigned crew members
        if (assignment.getAssignedCrewId() != null) {
            List<CrewMember> crewMembers = crewMemberRepository.findActiveMembers(assignment.getAssignedCrewId());
            
            String message = String.format("La novedad ha superado la fecha estimada de resolución: %s. " +
                    "Se requiere atención urgente.",
                    assignment.getEstimatedResolutionDate());
            
            for (CrewMember member : crewMembers) {
                createNotification(
                    member.getUserId(), 
                    novelty, 
                    "NOVELTY_OVERDUE",
                    "Novedad vencida",
                    message
                );
            }
        }
        
        // TODO: También notificar a administradores sobre la novedad vencida
    }

    // Private helper method to create notification
    private void createNotification(Long userId, Novelty novelty, String type, String title, String message) {
        // Skip notification if userId is null (for now, until we implement admin user lookup)
        if (userId == null) {
            log.warn("Intentando crear notificación con userId null. Type: {}, Title: {}", type, title);
            return;
        }
        
        log.debug("Creando notificación - UserId: {}, Type: {}, Title: {}", userId, type, title);
        
        try {
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
            
            Notification savedNotification = notificationRepository.save(notification);
            log.info("Notificación creada exitosamente con ID: {} para usuario: {}", 
                    savedNotification.getId(), userId);
        } catch (Exception e) {
            log.error("Error al guardar notificación para usuario {}: {}", userId, e.getMessage(), e);
            throw e; // Re-throw para que se propague
        }
    }
}
