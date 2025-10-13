package co.com.ebsa.ebsa_nexus.infrastructure.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import co.com.ebsa.ebsa_nexus.domain.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * UserRepository usando Spring Data JPA
 * Extendido con métodos para gestión completa de usuarios
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("SELECT u FROM User u JOIN FETCH u.role LEFT JOIN FETCH u.workRole WHERE u.username = :username AND u.active = true")
    Optional<User> findByUsernameAndActiveTrue(@Param("username") String username);

    @Query("SELECT u FROM User u JOIN FETCH u.role LEFT JOIN FETCH u.workRole WHERE u.id = :id AND u.active = true")
    Optional<User> findByIdAndActiveTrue(@Param("id") Integer id);

    @Query("SELECT u FROM User u JOIN FETCH u.role LEFT JOIN FETCH u.workRole WHERE u.email = :email AND u.active = true")
    Optional<User> findByEmailAndActiveTrue(@Param("email") String email);
    
    // Métodos adicionales para gestión de usuarios
    
    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);
    
    @Query("SELECT u FROM User u WHERE u.username = :username")
    Optional<User> findByUsername(@Param("username") String username);
    
    @Query("SELECT u FROM User u WHERE u.active = true")
    Page<User> findByActiveTrue(Pageable pageable);
    
    @Query("SELECT u FROM User u JOIN u.role r WHERE r.name = :roleName")
    List<User> findByRoleName(@Param("roleName") String roleName);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.username = :username")
    boolean existsByUsername(@Param("username") String username);
    
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email")
    boolean existsByEmail(@Param("email") String email);
}
