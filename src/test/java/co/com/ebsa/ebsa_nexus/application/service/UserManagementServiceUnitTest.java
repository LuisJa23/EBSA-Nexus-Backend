package co.com.ebsa.ebsa_nexus.application.service;

import co.com.ebsa.ebsa_nexus.application.dto.request.auth.ChangePasswordRequest;
import co.com.ebsa.ebsa_nexus.application.dto.request.auth.CreateUserRequest;
import co.com.ebsa.ebsa_nexus.application.dto.request.auth.UpdateUserRequest;
import co.com.ebsa.ebsa_nexus.application.dto.response.UserResponse;
import co.com.ebsa.ebsa_nexus.domain.entity.Role;
import co.com.ebsa.ebsa_nexus.domain.entity.User;
import co.com.ebsa.ebsa_nexus.domain.entity.WorkRole;
import co.com.ebsa.ebsa_nexus.domain.exception.auth.InvalidPasswordException;
import co.com.ebsa.ebsa_nexus.domain.exception.auth.InvalidWorkRoleException;
import co.com.ebsa.ebsa_nexus.domain.exception.auth.MultipleDuplicateFieldsException;
import co.com.ebsa.ebsa_nexus.domain.exception.auth.UnauthorizedOperationException;
import co.com.ebsa.ebsa_nexus.domain.repository.UserDomainRepository;
import co.com.ebsa.ebsa_nexus.infrastructure.persistence.jpa.repositories.RoleRepository;
import co.com.ebsa.ebsa_nexus.infrastructure.persistence.jpa.repositories.WorkRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserManagementServiceUnitTest {

    @Mock
    private UserDomainRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private WorkRoleRepository workRoleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserManagementService service;

    private Role adminRole;
    private Role userRole;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        service = new UserManagementService(userRepository, roleRepository, workRoleRepository, passwordEncoder);
        adminRole = Role.builder().id(1L).name("ADMIN").build();
        userRole = Role.builder().id(2L).name("USER").build();
    }

    private User makeUser(Long id, String email, Long roleId) {
        return User.builder()
                .id(id)
                .uuid(UUID.randomUUID().toString())
                .username("u" + id)
                .email(email)
                .pwdHash("hash")
                .firstName("F")
                .lastName("L")
                .roleId(roleId)
                .active(true)
                .build();
    }

    @Test
    void createUser_success_nonAdminRole() {
        CreateUserRequest req = new CreateUserRequest(
                "newuser", // username
                "new@example.com", // email
                "Password1!", // password
                "F", // firstName
                "L", // lastName
                "USER", // roleName
                null, // workRoleName
                "intern", // workType
                "123", // documentNumber
                "555" // phone
        );

        when(userRepository.findByEmail("admin@corp.com")).thenReturn(Optional.of(makeUser(10L, "admin@corp.com", 1L)));
        when(roleRepository.findById(1L)).thenReturn(Optional.of(adminRole));
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));

        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoder.encode("Password1!")).thenReturn("ENC");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(99L); return u; });

        UserResponse resp = service.createUser(req, "admin@corp.com");
        assertThat(resp.email()).isEqualTo("new@example.com");
        assertThat(resp.roleName()).isEqualTo("USER");
    }

    @Test
    void createUser_rejectsAdminRole() {
        CreateUserRequest req = new CreateUserRequest(
                "x", "x@example.com", "pass", "F", "L", "ADMIN", null, "intern", null, null);
        when(userRepository.findByEmail("admin@corp.com")).thenReturn(Optional.of(makeUser(1L, "admin@corp.com", 1L)));
        when(roleRepository.findById(1L)).thenReturn(Optional.of(adminRole));

        assertThatThrownBy(() -> service.createUser(req, "admin@corp.com"))
                .isInstanceOf(UnauthorizedOperationException.class);
    }

    @Test
    void createUser_duplicateFields_accumulates() {
        CreateUserRequest req = new CreateUserRequest(
                "dup", "dup@example.com", "pass", "F", "L", "USER", null, "intern", "123", "555");

        when(userRepository.findByEmail("admin@corp.com")).thenReturn(Optional.of(makeUser(1L, "admin@corp.com", 1L)));
        when(roleRepository.findById(1L)).thenReturn(Optional.of(adminRole));
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));

        when(userRepository.existsByEmail("dup@example.com")).thenReturn(true);
        when(userRepository.existsByUsername("dup")).thenReturn(true);
        when(userRepository.existsByDocumentNumber("123")).thenReturn(true);
        when(userRepository.existsByPhone("555")).thenReturn(true);

        assertThatThrownBy(() -> service.createUser(req, "admin@corp.com"))
                .isInstanceOf(MultipleDuplicateFieldsException.class);
    }

    @Test
    void updateUser_preventSelfDeactivate() {
        User admin = makeUser(1L, "admin@corp.com", 1L);
        User target = makeUser(1L, "admin@corp.com", 1L);

        when(userRepository.findByEmail("admin@corp.com")).thenReturn(Optional.of(admin));
        when(roleRepository.findById(1L)).thenReturn(Optional.of(adminRole));
        when(userRepository.findById(1L)).thenReturn(Optional.of(target));

        UpdateUserRequest req = new UpdateUserRequest(
                null, // username
                null, // email
                null, // password
                null, // firstName
                null, // lastName
                null, // roleId
                null, // workRoleId
                null, // workType
                null, // documentNumber
                null, // phone
                false // active
        );
        assertThatThrownBy(() -> service.updateUser(1L, req, "admin@corp.com"))
                .isInstanceOf(UnauthorizedOperationException.class)
                .hasMessageContaining("desactivar tu propia cuenta");
    }

    @Test
    void validateWorkRoleMismatch_throws() {
        User admin = makeUser(1L, "admin@corp.com", 1L);
        when(userRepository.findByEmail("admin@corp.com")).thenReturn(Optional.of(admin));
        when(roleRepository.findById(1L)).thenReturn(Optional.of(adminRole));

        WorkRole wr = WorkRole.builder().id(5L).name("ExternalRole").type(WorkRole.WorkRoleType.extern).build();
        when(workRoleRepository.findByName("ExternalRole")).thenReturn(Optional.of(wr));
        when(workRoleRepository.findById(5L)).thenReturn(Optional.of(wr));
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));

        CreateUserRequest req = new CreateUserRequest(
                "ab", "a@b.com", "pass", "F", "L", "USER", "ExternalRole", "intern", null, null);

        assertThatThrownBy(() -> service.createUser(req, "admin@corp.com"))
                .isInstanceOf(InvalidWorkRoleException.class);
    }

    @Test
    void changePassword_happyPath() {
        User u = makeUser(2L, "user@corp.com", 2L);
        u.setPwdHash("ENC_OLD");

        when(userRepository.findByEmail("user@corp.com")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("old", "ENC_OLD")).thenReturn(true);

        ChangePasswordRequest req = new ChangePasswordRequest("old", "new1", "new1");
        service.changePassword(req, "user@corp.com");

        verify(userRepository).save(any(User.class));
    }

    @Test
    void changePassword_currentIncorrect_throws() {
        User u = makeUser(2L, "user@corp.com", 2L);
        u.setPwdHash("ENC_OLD");
        when(userRepository.findByEmail("user@corp.com")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("wrong", "ENC_OLD")).thenReturn(false);

        ChangePasswordRequest req = new ChangePasswordRequest("wrong", "new1", "new1");
        assertThatThrownBy(() -> service.changePassword(req, "user@corp.com"))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessageContaining("incorrecta");
    }

    @Test
    void changePassword_newsDontMatch_throws() {
        ChangePasswordRequest req = new ChangePasswordRequest("old", "a", "b");
        assertThatThrownBy(() -> service.changePassword(req, "user@corp.com"))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessageContaining("no coinciden");
    }

}
