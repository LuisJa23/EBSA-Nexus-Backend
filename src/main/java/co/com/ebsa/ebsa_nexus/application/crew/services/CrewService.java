package co.com.ebsa.ebsa_nexus.application.crew.services;

import co.com.ebsa.ebsa_nexus.application.crew.exceptions.CrewNotFoundException;
import co.com.ebsa.ebsa_nexus.application.crew.exceptions.InvalidCrewStatusException;
import co.com.ebsa.ebsa_nexus.application.crew.factories.CrewFactory;
import co.com.ebsa.ebsa_nexus.domain.crew.entities.Crew;
import co.com.ebsa.ebsa_nexus.domain.crew.enums.CrewStatus;
import co.com.ebsa.ebsa_nexus.domain.crew.repositories.CrewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de aplicación para gestión de cuadrillas.
 * 
 * <p>Implementa la lógica de negocio relacionada con:
 * <ul>
 *   <li>Creación y eliminación de cuadrillas</li>
 *   <li>Actualización de información de cuadrillas</li>
 *   <li>Cambio de estados</li>
 *   <li>Consultas y búsquedas</li>
 * </ul>
 * 
 * @author Luis Javier
 * @version 1.0
 * @since 2025-10-16
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CrewService {
    
    private final CrewRepository crewRepository;
    private final CrewFactory crewFactory;
    
    /**
     * Crea una nueva cuadrilla.
     * 
     * @param name Nombre de la cuadrilla
     * @param description Descripción opcional
     * @param createdBy ID del usuario creador
     * @return Cuadrilla creada
     * @throws IllegalArgumentException si los parámetros son inválidos
     */
    public Crew createCrew(String name, String description, Long createdBy) {
        log.info("Creating new crew: name={}, createdBy={}", name, createdBy);
        
        Crew crew = crewFactory.createCrew(name, description, createdBy);
        Crew saved = crewRepository.save(crew);
        
        log.info("Crew created successfully: id={}, name={}", saved.getId(), saved.getName());
        return saved;
    }
    
    /**
     * Obtiene una cuadrilla por ID.
     * 
     * @param id ID de la cuadrilla
     * @return Cuadrilla encontrada
     * @throws CrewNotFoundException si no se encuentra
     */
    @Transactional(readOnly = true)
    public Crew getCrewById(Long id) {
        log.debug("Getting crew by id: {}", id);
        return crewRepository.findById(id)
                .orElseThrow(() -> new CrewNotFoundException(id));
    }
    
    /**
     * Obtiene una cuadrilla activa por ID.
     * 
     * @param id ID de la cuadrilla
     * @return Cuadrilla activa encontrada
     * @throws CrewNotFoundException si no se encuentra o está eliminada
     */
    @Transactional(readOnly = true)
    public Crew getActiveCrewById(Long id) {
        log.debug("Getting active crew by id: {}", id);
        return crewRepository.findActiveById(id)
                .orElseThrow(() -> new CrewNotFoundException(id));
    }
    
    /**
     * Lista todas las cuadrillas activas.
     * 
     * @return Lista de cuadrillas activas
     */
    @Transactional(readOnly = true)
    public List<Crew> getAllActiveCrews() {
        log.debug("Getting all active crews");
        return crewRepository.findAllActive();
    }
    
    /**
     * Lista cuadrillas por estado.
     * 
     * @param status Estado a filtrar
     * @return Lista de cuadrillas con ese estado
     */
    @Transactional(readOnly = true)
    public List<Crew> getCrewsByStatus(CrewStatus status) {
        log.debug("Getting crews by status: {}", status);
        return crewRepository.findByStatus(status);
    }
    
    /**
     * Lista cuadrillas disponibles para asignación.
     * 
     * @return Lista de cuadrillas disponibles
     */
    @Transactional(readOnly = true)
    public List<Crew> getAvailableCrews() {
        log.debug("Getting available crews");
        return crewRepository.findAvailableCrews();
    }
    
    /**
     * Lista cuadrillas creadas por un usuario.
     * 
     * @param userId ID del usuario
     * @return Lista de cuadrillas creadas por el usuario
     */
    @Transactional(readOnly = true)
    public List<Crew> getCrewsCreatedBy(Long userId) {
        log.debug("Getting crews created by user: {}", userId);
        return crewRepository.findByCreatedBy(userId);
    }
    
    /**
     * Actualiza la información básica de una cuadrilla.
     * Solo permite actualizar si está en estado DISPONIBLE.
     * 
     * @param id ID de la cuadrilla
     * @param name Nuevo nombre
     * @param description Nueva descripción
     * @return Cuadrilla actualizada
     * @throws CrewNotFoundException si no se encuentra
     * @throws InvalidCrewStatusException si no está disponible
     */
    public Crew updateCrewInfo(Long id, String name, String description) {
        log.info("Updating crew info: id={}", id);
        
        Crew crew = getActiveCrewById(id);
        
        if (!crew.allowsMemberModifications()) {
            throw new InvalidCrewStatusException(crew.getStatus(), "update crew information");
        }
        
        if (name != null && !name.trim().isEmpty()) {
            crew.setName(name.trim());
        }
        
        if (description != null) {
            crew.setDescription(description.trim());
        }
        
        crew.setUpdatedAt(LocalDateTime.now());
        
        Crew updated = crewRepository.save(crew);
        log.info("Crew info updated successfully: id={}", id);
        
        return updated;
    }
    
    /**
     * Cambia el estado de una cuadrilla.
     * 
     * @param id ID de la cuadrilla
     * @param newStatus Nuevo estado
     * @return Cuadrilla con estado actualizado
     * @throws CrewNotFoundException si no se encuentra
     */
    public Crew changeCrewStatus(Long id, CrewStatus newStatus) {
        log.info("Changing crew status: id={}, newStatus={}", id, newStatus);
        
        Crew crew = getActiveCrewById(id);
        crew.changeStatus(newStatus);
        
        Crew updated = crewRepository.save(crew);
        log.info("Crew status changed successfully: id={}, status={}", id, newStatus);
        
        return updated;
    }
    
    /**
     * Marca una cuadrilla como disponible.
     * 
     * @param id ID de la cuadrilla
     * @return Cuadrilla actualizada
     */
    public Crew markAsAvailable(Long id) {
        return changeCrewStatus(id, CrewStatus.DISPONIBLE);
    }
    
    /**
     * Marca una cuadrilla como en atención.
     * 
     * @param id ID de la cuadrilla
     * @return Cuadrilla actualizada
     */
    public Crew markAsInAttention(Long id) {
        return changeCrewStatus(id, CrewStatus.EN_ATENCION);
    }
    
    /**
     * Elimina lógicamente una cuadrilla.
     * Solo permite eliminar si está DISPONIBLE.
     * 
     * @param id ID de la cuadrilla
     * @throws CrewNotFoundException si no se encuentra
     * @throws InvalidCrewStatusException si no está disponible
     */
    public void deleteCrew(Long id) {
        log.info("Deleting crew: id={}", id);
        
        Crew crew = getActiveCrewById(id);
        
        if (!crew.isAvailable()) {
            throw new InvalidCrewStatusException(crew.getStatus(), "delete crew");
        }
        
        crew.markAsDeleted();
        crewRepository.save(crew);
        
        log.info("Crew deleted successfully: id={}", id);
    }
    
    /**
     * Verifica si una cuadrilla existe.
     * 
     * @param id ID de la cuadrilla
     * @return true si existe, false en caso contrario
     */
    @Transactional(readOnly = true)
    public boolean crewExists(Long id) {
        return crewRepository.existsById(id);
    }
    
    /**
     * Cuenta el número de cuadrillas activas.
     * 
     * @return Número de cuadrillas activas
     */
    @Transactional(readOnly = true)
    public long countActiveCrews() {
        return crewRepository.countActiveCrews();
    }
    
    /**
     * Cuenta cuadrillas por estado.
     * 
     * @param status Estado a contar
     * @return Número de cuadrillas en ese estado
     */
    @Transactional(readOnly = true)
    public long countCrewsByStatus(CrewStatus status) {
        return crewRepository.countByStatus(status);
    }
}
