package co.com.ebsa.ebsa_nexus.domain.enums;

/**
 * Motivos por los cuales se crea una novedad en el sistema de gestión de cuadrillas.
 * 
 * <p>Cubre diferentes tipos de fallas y situaciones que requieren atención
 * por parte de las cuadrillas de mantenimiento eléctrico.</p>
 * 
 * @author EBSA Nexus Team
 * @version 2.0
 * @since 2025-01-21
 */
public enum NoveltyReason {
    /**
     * Falla en equipos eléctricos (transformadores, interruptores, etc.).
     */
    EQUIPMENT_FAILURE("Falla de Equipo"),
    
    /**
     * Corte o interrupción del servicio eléctrico.
     */
    POWER_OUTAGE("Corte de Energía"),
    
    /**
     * Daños en cables, postes o infraestructura de red.
     */
    INFRASTRUCTURE_DAMAGE("Daño en Infraestructura"),
    
    /**
     * Mantenimiento preventivo programado.
     */
    PREVENTIVE_MAINTENANCE("Mantenimiento Preventivo"),
    
    /**
     * Emergencia que requiere atención inmediata.
     */
    EMERGENCY("Emergencia"),
    
    /**
     * Instalación de nuevos equipos o medidores.
     */
    INSTALLATION("Instalación"),
    
    /**
     * Inspección de rutina o verificación.
     */
    INSPECTION("Inspección"),
    
    /**
     * Error en la lectura del medidor eléctrico.
     */
    READING_ERROR("Error de Lectura"),
    
    /**
     * Requiere actualización de datos del cliente o medidor.
     */
    DATA_UPDATE("Actualización de Datos"),
    
    /**
     * Otros motivos no especificados.
     */
    OTHER("Otros");
    
    private final String displayName;
    
    NoveltyReason(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * Obtiene el nombre descriptivo del motivo.
     * 
     * @return Nombre para mostrar al usuario
     */
    public String getDisplayName() {
        return displayName;
    }
}
