package co.com.ebsa.ebsa_nexus.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import co.com.ebsa.ebsa_nexus.domain.entity.User;

import java.util.Optional;

/**
 * UserRepository usando Spring Data JPA
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("SELECT u FROM User u JOIN FETCH u.role LEFT JOIN FETCH u.workRole WHERE u.username = :username AND u.active = true")
    Optional<User> findByUsernameAndActiveTrue(@Param("username") String username);

    @Query("SELECT u FROM User u JOIN FETCH u.role LEFT JOIN FETCH u.workRole WHERE u.id = :id AND u.active = true")
    Optional<User> findByIdAndActiveTrue(@Param("id") Integer id);

    @Query("SELECT u FROM User u JOIN FETCH u.role LEFT JOIN FETCH u.workRole WHERE u.email = :email AND u.active = true")
    Optional<User> findByEmailAndActiveTrue(@Param("email") String email);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
