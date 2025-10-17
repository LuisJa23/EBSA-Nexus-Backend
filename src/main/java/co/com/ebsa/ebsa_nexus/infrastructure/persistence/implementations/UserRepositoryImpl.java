package co.com.ebsa.ebsa_nexus.infrastructure.persistence.implementations;

import co.com.ebsa.ebsa_nexus.domain.entity.User;
import co.com.ebsa.ebsa_nexus.domain.repository.UserDomainRepository;
import co.com.ebsa.ebsa_nexus.infrastructure.persistence.jpa.repositories.UserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Implementación del repositorio de dominio UserDomainRepository.
 * Esta clase pertenece a la capa de infraestructura y actúa como adaptador
 * entre la capa de dominio y la implementación JPA específica.
 */
@Repository
public class UserRepositoryImpl implements UserDomainRepository {
    
    private final UserRepository jpaUserRepository;
    
    public UserRepositoryImpl(@Lazy UserRepository jpaUserRepository) {
        this.jpaUserRepository = jpaUserRepository;
    }
    
    @Override
    public User save(User user) {
        return jpaUserRepository.save(user);
    }
    
    @Override
    public Optional<User> findById(Long id) {
        return jpaUserRepository.findById(id);
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        // Buscar por email sin restricción de activo para validaciones de admin
        return jpaUserRepository.findByEmail(email);
    }
    
    @Override
    public Optional<User> findByUsername(String username) {
        // Buscar por username sin restricción de activo para validaciones
        return jpaUserRepository.findByUsername(username);
    }
    
    @Override
    public Page<User> findAll(Pageable pageable) {
        return jpaUserRepository.findAll(pageable);
    }
    
    @Override
    public Page<User> findByActiveTrue(Pageable pageable) {
        return jpaUserRepository.findByActiveTrue(pageable);
    }
    
    @Override
    public List<User> findByRoleName(String roleName) {
        return jpaUserRepository.findByRoleName(roleName);
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return jpaUserRepository.existsByEmail(email);
    }
    
    @Override
    public boolean existsByUsername(String username) {
        return jpaUserRepository.existsByUsername(username);
    }
    
    @Override
    public boolean existsByEmailAndIdNot(String email, Long id) {
        return jpaUserRepository.existsByEmailAndIdNot(email, id);
    }
    
    @Override
    public boolean existsByUsernameAndIdNot(String username, Long id) {
        return jpaUserRepository.existsByUsernameAndIdNot(username, id);
    }
    
    @Override
    public boolean existsByDocumentNumber(String documentNumber) {
        return jpaUserRepository.existsByDocumentNumber(documentNumber);
    }
    
    @Override
    public boolean existsByDocumentNumberAndIdNot(String documentNumber, Long id) {
        return jpaUserRepository.existsByDocumentNumberAndIdNot(documentNumber, id);
    }
    
    @Override
    public boolean existsByPhone(String phone) {
        return jpaUserRepository.existsByPhone(phone);
    }
    
    @Override
    public boolean existsByPhoneAndIdNot(String phone, Long id) {
        return jpaUserRepository.existsByPhoneAndIdNot(phone, id);
    }
    
    @Override
    public List<User> findUsersWithoutActiveCrew() {
        return jpaUserRepository.findUsersWithoutActiveCrew();
    }
    
    @Override
    public void delete(User user) {
        jpaUserRepository.delete(user);
    }
}