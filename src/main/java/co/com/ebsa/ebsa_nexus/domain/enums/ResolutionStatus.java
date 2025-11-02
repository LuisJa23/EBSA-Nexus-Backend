package co.com.ebsa.ebsa_nexus.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Estado resultante de una novedad tras generar un reporte.
 * 
 * <p>Determina cómo debe cambiar el estado de la novedad
 * después de generar el reporte de resolución.</p>
 * 
 * @author EBSA Nexus Team
 * @version 1.0
 * @since 2025-10-28
 */
@Getter
@RequiredArgsConstructor
public enum ResolutionStatus {
    
    /**
     * La novedad fue completada exitosamente.
     * La novedad pasa a estado COMPLETADA.
     */
    COMPLETADA("Completada", "La novedad fue resuelta exitosamente"),
    
    /**
     * La novedad no pudo ser completada.
     * La novedad permanece EN_CURSO para nueva asignación.
     */
    NO_COMPLETADA("No Completada", "La novedad requiere trabajo adicional o reasignación"),
    
    /**
     * La novedad se cierra definitivamente.
     * La novedad pasa a estado CERRADA.
     */
    CERRADA("Cerrada", "La novedad se cierra definitivamente");
    
    private final String displayName;
    private final String description;
    
    /**
     * Obtiene el estado de novedad correspondiente.
     */
    public NoveltyStatus toNoveltyStatus() {
        return switch (this) {
            case COMPLETADA -> NoveltyStatus.COMPLETADA;
            case NO_COMPLETADA -> NoveltyStatus.EN_CURSO;
            case CERRADA -> NoveltyStatus.CERRADA;
        };
    }
}
