package com.walletx.walletservice.controller;

import com.walletx.walletservice.business.WalletService;
import com.walletx.walletservice.business.DepositResult;
import com.walletx.walletservice.config.SecurityConfig;
import com.walletx.walletservice.domain.WalletCurrency;
import com.walletx.walletservice.domain.WalletEntity;
import com.walletx.walletservice.event.DepositCompletedEvent;
import com.walletx.walletservice.security.CurrentUserPrincipal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;
import java.time.Instant;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WalletController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = {
        "walletx.jwt.secret=walletx-test-signing-secret-must-be-at-least-32-bytes",
        "walletx.internal-transfer-token=walletx-test-internal-transfer-token"
})
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

        mockMvc.perform(post("/api/wallets/{walletId}/deposit", UUID.randomUUID())
                        .contentType("application/json")
                        .content("{\"amount\":10.00,\"currency\":\"RON\"}"))
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

    @Test
    void depositsForAuthenticatedWalletOwnerAndReturnsCompletedDeposit() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID walletId = UUID.randomUUID();
        UUID depositId = UUID.randomUUID();
        WalletEntity wallet = new WalletEntity(userId);
        wallet.setBalance(new BigDecimal("12.50"));
        Instant completedAt = Instant.parse("2026-10-04T12:00:00Z");
        DepositCompletedEvent event = new DepositCompletedEvent(
                depositId, walletId, userId, new BigDecimal("12.50"), WalletCurrency.RON, completedAt);
        when(walletService.deposit(userId, walletId, new BigDecimal("12.50"), WalletCurrency.RON))
                .thenReturn(new DepositResult(wallet, event));

        mockMvc.perform(post("/api/wallets/{walletId}/deposit", walletId)
                        .with(authentication(new UsernamePasswordAuthenticationToken(
                                new CurrentUserPrincipal(userId),
                                null,
                                java.util.Collections.emptyList())))
                        .contentType("application/json")
                        .content("{\"amount\":12.50,\"currency\":\"RON\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.depositId").value(depositId.toString()))
                .andExpect(jsonPath("$.walletId").value(walletId.toString()))
                .andExpect(jsonPath("$.amount").value(12.50))
                .andExpect(jsonPath("$.currency").value("RON"))
                .andExpect(jsonPath("$.balance").value(12.50))
                .andExpect(jsonPath("$.completedAt").value("2026-10-04T12:00:00Z"));

        verify(walletService).deposit(userId, walletId, new BigDecimal("12.50"), WalletCurrency.RON);
    }

    @Test
    void executesInternalTransferOnlyWithServiceTokenAndAuthenticatedOwner() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID senderWalletId = UUID.randomUUID();
        UUID receiverWalletId = UUID.randomUUID();

        mockMvc.perform(post("/api/internal/transfers")
                        .with(authentication(new UsernamePasswordAuthenticationToken(
                                userId, null, java.util.Collections.emptyList())))
                        .header("X-Internal-Transfer-Token", "walletx-test-internal-transfer-token")
                        .contentType("application/json")
                        .content("""
                                {
                                  "senderWalletId": "%s",
                                  "receiverWalletId": "%s",
                                  "amount": 12.50,
                                  "currency": "RON"
                                }
                                """.formatted(senderWalletId, receiverWalletId)))
                .andExpect(status().isNoContent());

        verify(walletService).transfer(
                userId, senderWalletId, receiverWalletId, new BigDecimal("12.50"), WalletCurrency.RON);
    }

    @Test
    void rejectsInternalTransferWithIncorrectServiceToken() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(post("/api/internal/transfers")
                        .with(authentication(new UsernamePasswordAuthenticationToken(
                                userId, null, java.util.Collections.emptyList())))
                        .header("X-Internal-Transfer-Token", "incorrect-token")
                        .contentType("application/json")
                        .content("""
                                {
                                  "senderWalletId": "%s",
                                  "receiverWalletId": "%s",
                                  "amount": 12.50,
                                  "currency": "RON"
                                }
                                """.formatted(UUID.randomUUID(), UUID.randomUUID())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rejectsInvalidDepositRequest() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID walletId = UUID.randomUUID();
        var authentication = authentication(new UsernamePasswordAuthenticationToken(
                new CurrentUserPrincipal(userId),
                null,
                java.util.Collections.emptyList()));

        mockMvc.perform(post("/api/wallets/{walletId}/deposit", walletId)
                        .with(authentication)
                        .contentType("application/json")
                        .content("{\"amount\":0,\"currency\":\"RON\"}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/wallets/{walletId}/deposit", walletId)
                        .with(authentication)
                        .contentType("application/json")
                        .content("{\"amount\":1,\"currency\":\"USD\"}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/wallets/{walletId}/deposit", walletId)
                        .with(authentication)
                        .contentType("application/json")
                        .content("{\"amount\":1.001,\"currency\":\"RON\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsDepositWhenWalletIsNotOwnedByAuthenticatedUser() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID walletId = UUID.randomUUID();
        doThrow(new org.springframework.security.access.AccessDeniedException("forbidden"))
                .when(walletService).deposit(userId, walletId, new BigDecimal("1.00"), WalletCurrency.RON);

        mockMvc.perform(post("/api/wallets/{walletId}/deposit", walletId)
                        .with(authentication(new UsernamePasswordAuthenticationToken(
                                new CurrentUserPrincipal(userId),
                                null,
                                java.util.Collections.emptyList())))
                        .contentType("application/json")
                        .content("{\"amount\":1.00,\"currency\":\"RON\"}"))
                .andExpect(status().isForbidden());
    }
}
