package co.com.ebsa.ebsa_nexus.domain.enums;

/**
 * Motivos por los cuales se crea una novedad.
 * Coincide exactamente con las opciones del formulario.
 * 
 * @author EBSA Nexus Team
 * @version 2.0
 * @since 2025-10-22
 */
public enum NoveltyReason {
    /**
     * Error en la lectura del medidor
     */
    ERROR_LECTURA("Error de lectura"),
    
    /**
     * Actualización de datos del cliente o medidor
     */
    ACTUALIZACION_DATOS("Actualización de datos"),
    
    /**
     * Otros motivos no especificados
     */
    OTROS("Otros");
    
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
