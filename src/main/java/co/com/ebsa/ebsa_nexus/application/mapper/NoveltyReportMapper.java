package co.com.ebsa.ebsa_nexus.application.mapper;

import co.com.ebsa.ebsa_nexus.application.dto.response.NoveltyReportResponse;
import co.com.ebsa.ebsa_nexus.domain.entity.NoveltyReport;
import co.com.ebsa.ebsa_nexus.domain.entity.ReportParticipant;
import co.com.ebsa.ebsa_nexus.domain.entity.User;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Mapper para transformar entidades NoveltyReport en DTOs.
 * 
 * @author EBSA Nexus Team
 * @version 1.0
 * @since 2025-10-28
 */
@Component
public class NoveltyReportMapper {
    
    /**
     * Convierte una entidad NoveltyReport a DTO de respuesta.
     * 
     * @param report Entidad a convertir
     * @return DTO de respuesta
     */
    public NoveltyReportResponse toResponse(NoveltyReport report) {
        if (report == null) {
            return null;
        }
        
        return NoveltyReportResponse.builder()
            .id(report.getId())
            .noveltyId(report.getNovelty() != null ? report.getNovelty().getId() : null)
            .generatedBy(mapUserToSummary(report.getGeneratedBy()))
            .reportContent(report.getReportContent())
            .observations(report.getObservations())
            .workStartDate(report.getWorkStartDate())
            .workEndDate(report.getWorkEndDate())
            .resolutionStatus(report.getResolutionStatus())
            .participants(report.getParticipants() != null ? 
                report.getParticipants().stream()
                    .map(this::mapParticipantToResponse)
                    .collect(Collectors.toList()) : 
                null)
            .createdAt(report.getCreatedAt())
            .build();
    }
    
    /**
     * Mapea un usuario a un resumen básico.
     * 
     * @param user Usuario a mapear
     * @return Resumen del usuario
     */
    private NoveltyReportResponse.UserSummary mapUserToSummary(User user) {
        if (user == null) {
            return null;
        }
        
        return NoveltyReportResponse.UserSummary.builder()
            .id(user.getId())
            .fullName(user.getFirstName() + " " + user.getLastName())
            .email(user.getEmail())
            .build();
    }
    
    /**
     * Mapea un participante a DTO de respuesta.
     * 
     * @param participant Participante a mapear
     * @return DTO del participante
     */
    private NoveltyReportResponse.ParticipantResponse mapParticipantToResponse(ReportParticipant participant) {
        if (participant == null || participant.getUser() == null) {
            return null;
        }
        
        User user = participant.getUser();
        return NoveltyReportResponse.ParticipantResponse.builder()
            .userId(user.getId())
            .fullName(user.getFirstName() + " " + user.getLastName())
            .addedAt(participant.getAddedAt())
            .build();
    }
}
