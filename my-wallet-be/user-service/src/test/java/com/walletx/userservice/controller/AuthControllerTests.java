package com.walletx.userservice.controller;

import com.walletx.userservice.business.LoginRequest;
import com.walletx.userservice.business.LoginResponse;
import com.walletx.userservice.business.JwtService;
import com.walletx.userservice.business.UserAuthenticationService;
import com.walletx.userservice.business.UserRegistrationRequest;
import com.walletx.userservice.business.UserRegistrationResponse;
import com.walletx.userservice.business.UserRegistrationService;
import com.walletx.userservice.config.SecurityConfig;
import com.walletx.userservice.domain.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = AuthController.class, properties = "walletx.jwt.secret=walletx-test-signing-secret-must-be-at-least-32-bytes")
@Import({SecurityConfig.class, JwtService.class})
class AuthControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @MockBean
    private UserRegistrationService userRegistrationService;

    @MockBean
    private UserAuthenticationService userAuthenticationService;

    @Test
    void createsUserAtRegisterEndpointWithoutReturningCredentials() throws Exception {
        UUID userId = UUID.randomUUID();
        when(userRegistrationService.register(any(UserRegistrationRequest.class)))
                .thenReturn(new UserRegistrationResponse(userId, "Ada Lovelace", "ada@example.com"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Ada Lovelace",
                                  "email": "ada@example.com",
                                  "password": "securePass1"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.name").value("Ada Lovelace"))
                .andExpect(jsonPath("$.email").value("ada@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());

        verify(userRegistrationService).register(any(UserRegistrationRequest.class));
    }

    @Test
    void rejectsInvalidRegistrationInputBeforeCallingService() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": " ",
                                  "email": "not-an-email",
                                  "password": "short"
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userRegistrationService);
    }

    @Test
    void logsInUserAndReturnsJwt() throws Exception {
        LoginResponse response = new LoginResponse("jwt-token-value", "Bearer");
        when(userAuthenticationService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "ada@example.com",
                                  "password": "securePass1"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("jwt-token-value"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.id").doesNotExist())
                .andExpect(jsonPath("$.name").doesNotExist())
                .andExpect(jsonPath("$.email").doesNotExist());

        verify(userAuthenticationService).login(any(LoginRequest.class));
    }

    @Test
    void protectsUnlistedRoutesAndAcceptsValidBearerTokens() throws Exception {
        mockMvc.perform(get("/protected-resource"))
                .andExpect(status().isUnauthorized());

        UserEntity user = new UserEntity("Ada Lovelace", "ada@example.com", "password-hash");
        String accessToken = jwtService.generateToken(user);

        mockMvc.perform(get("/protected-resource")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void rejectsInvalidBearerTokenOnProtectedRoutes() throws Exception {
        mockMvc.perform(get("/protected-resource")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }
}
