package co.com.ebsa.ebsa_nexus.domain.repository;

import co.com.ebsa.ebsa_nexus.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz del repositorio de dominio para la entidad User.
 * Esta interfaz pertenece a la capa de dominio y define las operaciones de persistencia
 * necesarias para la gestión de usuarios sin depender de implementaciones específicas.
 */
public interface UserDomainRepository {
    
    /**
     * Guarda un usuario en el repositorio.
     * 
     * @param user Usuario a guardar
     * @return Usuario guardado con ID generado si es nuevo
     */
    User save(User user);
    
    /**
     * Busca un usuario por su ID.
     * 
     * @param id ID del usuario
     * @return Optional conteniendo el usuario si existe
     */
    Optional<User> findById(Long id);
    
    /**
     * Busca un usuario activo por su email.
     * 
     * @param email Email del usuario
     * @return Optional conteniendo el usuario si existe y está activo
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Busca un usuario activo por su username.
     * 
     * @param username Username del usuario
     * @return Optional conteniendo el usuario si existe y está activo
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Obtiene todos los usuarios con paginación.
     * 
     * @param pageable Información de paginación
     * @return Página de usuarios
     */
    Page<User> findAll(Pageable pageable);
    
    /**
     * Obtiene todos los usuarios activos con paginación.
     * 
     * @param pageable Información de paginación
     * @return Página de usuarios activos
     */
    Page<User> findByActiveTrue(Pageable pageable);
    
    /**
     * Busca usuarios por nombre de rol.
     * 
     * @param roleName Nombre del rol
     * @return Lista de usuarios con ese rol
     */
    List<User> findByRoleName(String roleName);
    
    /**
     * Verifica si existe un usuario con el email dado.
     * 
     * @param email Email a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByEmail(String email);
    
    /**
     * Verifica si existe un usuario con el username dado.
     * 
     * @param username Username a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByUsername(String username);
    
    /**
     * Verifica si existe un usuario con el email dado, excluyendo un usuario específico.
     * 
     * @param email Email a verificar
     * @param id ID del usuario a excluir de la búsqueda
     * @return true si existe otro usuario con ese email, false en caso contrario
     */
    boolean existsByEmailAndIdNot(String email, Long id);
    
    /**
     * Verifica si existe un usuario con el username dado, excluyendo un usuario específico.
     * 
     * @param username Username a verificar
     * @param id ID del usuario a excluir de la búsqueda
     * @return true si existe otro usuario con ese username, false en caso contrario
     */
    boolean existsByUsernameAndIdNot(String username, Long id);
    
    /**
     * Verifica si existe un usuario con el número de documento dado.
     * 
     * @param documentNumber Número de documento a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByDocumentNumber(String documentNumber);
    
    /**
     * Verifica si existe un usuario con el número de documento dado, excluyendo un usuario específico.
     * 
     * @param documentNumber Número de documento a verificar
     * @param id ID del usuario a excluir de la búsqueda
     * @return true si existe otro usuario con ese documento, false en caso contrario
     */
    boolean existsByDocumentNumberAndIdNot(String documentNumber, Long id);
    
    /**
     * Verifica si existe un usuario con el teléfono dado.
     * 
     * @param phone Teléfono a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByPhone(String phone);
    
    /**
     * Verifica si existe un usuario con el teléfono dado, excluyendo un usuario específico.
     * 
     * @param phone Teléfono a verificar
     * @param id ID del usuario a excluir de la búsqueda
     * @return true si existe otro usuario con ese teléfono, false en caso contrario
     */
    boolean existsByPhoneAndIdNot(String phone, Long id);
    
    /**
     * Encuentra usuarios que no están asignados a ningún equipo activo.
     * 
     * @return Lista de usuarios sin equipo activo
     */
    List<User> findUsersWithoutActiveCrew();
    
    /**
     * Elimina un usuario del repositorio.
     * 
     * @param user Usuario a eliminar
     */
    void delete(User user);
}