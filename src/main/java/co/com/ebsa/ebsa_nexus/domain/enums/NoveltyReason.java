package co.com.ebsa.ebsa_nexus.domain.enums;

/**
 * Motivos por los cuales se crea una novedad.
 * 
 * <p>Los motivos aplican de manera uniforme a todas las áreas
 * (Facturación, Cartera, Pérdidas).</p>
 * 
 * @author EBSA Nexus Team
 * @version 1.0
 * @since 2025-10-21
 */
public enum NoveltyReason {
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
