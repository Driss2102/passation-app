package com.passation.passation_backend.service;

import com.passation.passation_backend.dto.CreateUserRequest;
import com.passation.passation_backend.dto.UserDTO;
import com.passation.passation_backend.model.Role;
import com.passation.passation_backend.model.User;
import com.passation.passation_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .nom("Martin")
                .prenom("Alice")
                .email("alice@example.com")
                .password("encoded")
                .departement("IT")
                .poste("Dev")
                .role(Role.EMPLOYE)
                .build();
    }

    // ── findAll ──────────────────────────────────────────────────────────────

    @Test
    void findAll_returnsAllUsersAsDTOs() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDTO> result = userService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("alice@example.com");
    }

    @Test
    void findAll_returnsEmptyListWhenNoUsers() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserDTO> result = userService.findAll();

        assertThat(result).isEmpty();
    }

    // ── findById ─────────────────────────────────────────────────────────────

    @Test
    void findById_returnsUserDTO_whenFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDTO result = userService.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNom()).isEqualTo("Martin");
    }

    @Test
    void findById_throwsException_whenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found with id: 99");
    }

    // ── findByRole ────────────────────────────────────────────────────────────

    @Test
    void findByRole_returnsUsersWithGivenRole() {
        when(userRepository.findByRole(Role.EMPLOYE)).thenReturn(List.of(user));

        List<UserDTO> result = userService.findByRole(Role.EMPLOYE);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRole()).isEqualTo(Role.EMPLOYE);
    }

    // ── create ───────────────────────────────────────────────────────────────

    @Test
    void create_savesAndReturnsDTO() {
        CreateUserRequest request = new CreateUserRequest();
        request.setNom("Dupont");
        request.setPrenom("Bob");
        request.setEmail("bob@example.com");
        request.setPassword("pass");
        request.setDepartement("RH");
        request.setPoste("Manager");
        request.setRole(Role.MANAGER_RH);

        User saved = User.builder().id(2L).nom("Dupont").prenom("Bob")
                .email("bob@example.com").password("pass")
                .departement("RH").poste("Manager").role(Role.MANAGER_RH).build();
        when(userRepository.save(any(User.class))).thenReturn(saved);

        UserDTO result = userService.create(request);

        assertThat(result.getEmail()).isEqualTo("bob@example.com");
        assertThat(result.getRole()).isEqualTo(Role.MANAGER_RH);
        verify(userRepository).save(any(User.class));
    }

    // ── update ───────────────────────────────────────────────────────────────

    @Test
    void update_updatesAndReturnsDTO_whenFound() {
        CreateUserRequest request = new CreateUserRequest();
        request.setNom("Martin Updated");
        request.setPrenom("Alice");
        request.setEmail("alice.new@example.com");
        request.setPassword("");
        request.setDepartement("IT");
        request.setPoste("Lead Dev");
        request.setRole(Role.EMPLOYE);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDTO result = userService.update(1L, request);

        assertThat(result).isNotNull();
        verify(userRepository).save(any(User.class));
    }

    @Test
    void update_throwsException_whenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(99L, new CreateUserRequest()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found with id: 99");
    }

    // ── delete ───────────────────────────────────────────────────────────────

    @Test
    void delete_deletesUser_whenExists() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        userService.delete(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void delete_throwsException_whenNotFound() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> userService.delete(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found with id: 99");
    }

    // ── toDTO ─────────────────────────────────────────────────────────────────

    @Test
    void toDTO_returnsNull_whenUserIsNull() {
        UserDTO result = userService.toDTO(null);
        assertThat(result).isNull();
    }

    @Test
    void toDTO_mapsAllFields() {
        UserDTO dto = userService.toDTO(user);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getNom()).isEqualTo("Martin");
        assertThat(dto.getPrenom()).isEqualTo("Alice");
        assertThat(dto.getEmail()).isEqualTo("alice@example.com");
        assertThat(dto.getDepartement()).isEqualTo("IT");
        assertThat(dto.getPoste()).isEqualTo("Dev");
        assertThat(dto.getRole()).isEqualTo(Role.EMPLOYE);
    }
}
