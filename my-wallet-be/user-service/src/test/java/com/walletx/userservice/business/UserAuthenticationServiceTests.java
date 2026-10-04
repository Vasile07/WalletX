package com.walletx.userservice.business;

import com.walletx.userservice.domain.UserEntity;
import com.walletx.userservice.persistence.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAuthenticationServiceTests {

    @Mock
    private UserRepository userRepository;

    @Test
    void logsInUserAndReturnsJwt() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        JwtService jwtService = new JwtService("walletx-test-signing-secret-must-be-at-least-32-bytes", 3_600_000L);
        UserAuthenticationService service = new UserAuthenticationServiceImpl(userRepository, passwordEncoder, jwtService);

        UserEntity user = new UserEntity("Ada Lovelace", "ada@example.com", passwordEncoder.encode("securePass1"));

        when(userRepository.findByEmailIgnoreCase("ada@example.com")).thenReturn(Optional.of(user));

        LoginResponse response = service.login(new LoginRequest("ada@example.com", "securePass1"));

        assertTrue(jwtService.isTokenValid(response.getAccessToken()));
        assertEquals(user.getId().toString(), jwtService.parseClaims(response.getAccessToken()).getSubject());
        assertEquals("ada@example.com", jwtService.extractEmail(response.getAccessToken()));
        assertEquals("Ada Lovelace", jwtService.extractName(response.getAccessToken()));
        assertEquals("Bearer", response.getTokenType());
    }

    @Test
    void rejectsInvalidPassword() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        JwtService jwtService = new JwtService("walletx-test-signing-secret-must-be-at-least-32-bytes", 3_600_000L);
        UserAuthenticationService service = new UserAuthenticationServiceImpl(userRepository, passwordEncoder, jwtService);

        UserEntity user = new UserEntity("Ada Lovelace", "ada@example.com", passwordEncoder.encode("securePass1"));
        when(userRepository.findByEmailIgnoreCase("ada@example.com")).thenReturn(Optional.of(user));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.login(new LoginRequest("ada@example.com", "wrong-password")));

        assertEquals(401, exception.getStatusCode().value());
    }

    @Test
    void rejectsUnknownEmail() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        JwtService jwtService = new JwtService("walletx-test-signing-secret-must-be-at-least-32-bytes", 3_600_000L);
        UserAuthenticationService service = new UserAuthenticationServiceImpl(userRepository, passwordEncoder, jwtService);
        when(userRepository.findByEmailIgnoreCase("unknown@example.com")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.login(new LoginRequest("unknown@example.com", "securePass1")));

        assertEquals(401, exception.getStatusCode().value());
    }

    @Test
    void rejectsExpiredAndWrongSignatureTokens() throws InterruptedException {
        UserEntity user = new UserEntity("Ada Lovelace", "ada@example.com", "password-hash");
        JwtService shortLivedJwtService = new JwtService(
                "walletx-test-signing-secret-must-be-at-least-32-bytes",
                1
        );
        String expiredToken = shortLivedJwtService.generateToken(user);
        Thread.sleep(10);

        JwtService otherJwtService = new JwtService(
                "another-test-signing-secret-must-be-at-least-32-bytes",
                3_600_000L
        );

        assertFalse(shortLivedJwtService.isTokenValid(expiredToken));
        assertFalse(otherJwtService.isTokenValid(expiredToken));
    }
}
