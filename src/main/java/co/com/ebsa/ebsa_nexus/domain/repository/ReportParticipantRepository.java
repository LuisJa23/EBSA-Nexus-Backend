package co.com.ebsa.ebsa_nexus.domain.repository;

import co.com.ebsa.ebsa_nexus.domain.entity.ReportParticipant;

import java.util.List;

/**
 * Repositorio de dominio para ReportParticipant.
 * 
 * @author EBSA Nexus Team
 * @version 1.0
 * @since 2025-10-28
 */
public interface ReportParticipantRepository {
    
    /**
     * Guarda un participante de reporte.
     * 
     * @param participant Participante a guardar
     * @return Participante guardado
     */
    ReportParticipant save(ReportParticipant participant);
    
    /**
     * Busca todos los participantes de un reporte.
     * 
     * @param reportId ID del reporte
     * @return Lista de participantes
     */
    List<ReportParticipant> findByReportId(Long reportId);
    
    /**
     * Busca todos los reportes en los que participó un usuario.
     * 
     * @param userId ID del usuario
     * @return Lista de participaciones
     */
    List<ReportParticipant> findByUserId(Long userId);
    
    /**
     * Elimina todos los participantes de un reporte.
     * 
     * @param reportId ID del reporte
     */
    void deleteByReportId(Long reportId);
}
