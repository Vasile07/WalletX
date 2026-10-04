package com.walletx.walletservice.controller;

import com.walletx.walletservice.business.WalletService;
import com.walletx.walletservice.config.SecurityConfig;
import com.walletx.walletservice.domain.WalletEntity;
import com.walletx.walletservice.security.CurrentUserPrincipal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WalletController.class)
@Import(SecurityConfig.class)
class WalletControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WalletService walletService;

    @Test
    void retrievesWalletsForAuthenticatedUser() throws Exception {
        UUID userId = UUID.randomUUID();
        WalletEntity firstWallet = new WalletEntity(userId);
        WalletEntity secondWallet = new WalletEntity(userId);

        when(walletService.findAllByUserId(userId)).thenReturn(List.of(firstWallet, secondWallet));

        mockMvc.perform(get("/api/wallets")
                        .with(authentication(new UsernamePasswordAuthenticationToken(
                                new CurrentUserPrincipal(userId),
                                null,
                                java.util.Collections.emptyList()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(userId.toString()))
                .andExpect(jsonPath("$[1].userId").value(userId.toString()))
                .andExpect(jsonPath("$[0].currency").value("RON"));

        verify(walletService).findAllByUserId(userId);
    }

    @Test
    void createsWalletForAuthenticatedUser() throws Exception {
        UUID currentUserId = UUID.randomUUID();
        WalletEntity createdWallet = new WalletEntity(currentUserId);

        when(walletService.createWallet(currentUserId)).thenReturn(createdWallet);

        mockMvc.perform(post("/api/wallets")
                        .with(authentication(new UsernamePasswordAuthenticationToken(
                                new CurrentUserPrincipal(currentUserId),
                                null,
                                java.util.Collections.emptyList()))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(currentUserId.toString()))
                .andExpect(jsonPath("$.currency").value("RON"))
                .andExpect(jsonPath("$.balance").value(0.00));

        verify(walletService).createWallet(currentUserId);
    }

    @Test
    void rejectsUnauthenticatedRequests() throws Exception {
        mockMvc.perform(get("/api/wallets"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/wallets"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void resolvesAuthenticatedPrincipalToCurrentUserId() throws Exception {
        UUID userId = UUID.randomUUID();
        WalletEntity wallet = new WalletEntity(userId);
        when(walletService.findAllByUserId(userId)).thenReturn(List.of(wallet));

        mockMvc.perform(get("/api/wallets")
                        .with(authentication(new UsernamePasswordAuthenticationToken(
                                new CurrentUserPrincipal(userId),
                                null,
                                java.util.Collections.emptyList()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(userId.toString()));
    }
}
