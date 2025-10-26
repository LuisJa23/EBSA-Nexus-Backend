package co.com.ebsa.ebsa_nexus.domain.enums;

/**
 * Types of notifications in the system
 */
public enum NotificationType {
    NOVELTY_CREATED("Nueva novedad creada"),
    NOVELTY_ASSIGNED("Novedad asignada"),
    NOVELTY_STATUS_CHANGED("Estado de novedad actualizado"),
    NOVELTY_COMPLETED("Novedad completada"),
    CREW_ASSIGNED("Cuadrilla asignada"),
    SYSTEM_ALERT("Alerta del sistema"),
    REMINDER("Recordatorio"),
    GENERAL("Notificación general");
    
    private final String displayName;
    
    NotificationType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
