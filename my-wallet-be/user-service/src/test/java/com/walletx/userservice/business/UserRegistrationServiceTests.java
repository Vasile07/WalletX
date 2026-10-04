package com.walletx.userservice.business;

import com.walletx.userservice.domain.UserEntity;
import com.walletx.userservice.persistence.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRegistrationServiceTests {

    @Mock
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    void registersUserWithNormalizedEmailAndBcryptHash() {
        UserRegistrationService service = new UserRegistrationServiceImpl(userRepository, passwordEncoder);
        UserRegistrationRequest request = new UserRegistrationRequest("Ada Lovelace", " ADA@example.com ", "securePass1");
        when(userRepository.existsByEmailIgnoreCase("ada@example.com")).thenReturn(false);
        when(userRepository.saveAndFlush(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserRegistrationResponse response = service.register(request);

        ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).saveAndFlush(userCaptor.capture());
        UserEntity savedUser = userCaptor.getValue();
        assertEquals("ada@example.com", savedUser.getEmail());
        assertTrue(passwordEncoder.matches("securePass1", savedUser.getPasswordHash()));
        assertFalse(savedUser.getPasswordHash().equals("securePass1"));
        assertEquals(savedUser.getId(), response.getId());
        assertEquals("Ada Lovelace", response.getName());
        assertEquals("ada@example.com", response.getEmail());
    }

    @Test
    void rejectsDuplicateEmailWithoutSaving() {
        UserRegistrationService service = new UserRegistrationServiceImpl(userRepository, passwordEncoder);
        UserRegistrationRequest request = new UserRegistrationRequest("Ada Lovelace", "ada@example.com", "securePass1");
        when(userRepository.existsByEmailIgnoreCase("ada@example.com")).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.register(request)
        );

        assertEquals(409, exception.getStatusCode().value());
        verify(userRepository, never()).saveAndFlush(any(UserEntity.class));
    }

    @Test
    void rejectsConcurrentDuplicateEmailReportedByDatabase() {
        UserRegistrationService service = new UserRegistrationServiceImpl(userRepository, passwordEncoder);
        UserRegistrationRequest request = new UserRegistrationRequest("Ada Lovelace", "ada@example.com", "securePass1");
        when(userRepository.existsByEmailIgnoreCase("ada@example.com")).thenReturn(false);
        doThrow(new DataIntegrityViolationException("Unique email constraint"))
                .when(userRepository).saveAndFlush(any(UserEntity.class));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.register(request)
        );

        assertEquals(409, exception.getStatusCode().value());
    }

    @Test
    void rejectsPasswordsLongerThanBcryptByteLimit() {
        UserRegistrationService service = new UserRegistrationServiceImpl(userRepository, passwordEncoder);
        UserRegistrationRequest request = new UserRegistrationRequest("Ada Lovelace", "ada@example.com", "é".repeat(37));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.register(request)
        );

        assertEquals(400, exception.getStatusCode().value());
        verify(userRepository, never()).existsByEmailIgnoreCase(any());
    }
}
