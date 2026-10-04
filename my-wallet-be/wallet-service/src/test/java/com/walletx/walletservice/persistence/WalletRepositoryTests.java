package com.walletx.walletservice.persistence;

import com.walletx.walletservice.domain.WalletCurrency;
import com.walletx.walletservice.domain.DepositEntity;
import com.walletx.walletservice.domain.WalletEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class WalletRepositoryTests {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private DepositRepository depositRepository;

    @Test
    void persistsWalletDomainFields() {
        UUID userId = UUID.randomUUID();
        WalletEntity wallet = new WalletEntity(userId);

        WalletEntity savedWallet = walletRepository.saveAndFlush(wallet);
        WalletEntity loadedWallet = walletRepository.findById(savedWallet.getId()).orElseThrow();

        assertEquals(userId, loadedWallet.getUserId());
        assertEquals(WalletCurrency.RON, loadedWallet.getCurrency());
        assertEquals(wallet.getBalance(), loadedWallet.getBalance());
        assertTrue(walletRepository.existsById(wallet.getId()));
    }

    @Test
    void locksWalletByIdAndOwnerAndPersistsDepositHistory() {
        UUID userId = UUID.randomUUID();
        WalletEntity wallet = walletRepository.saveAndFlush(new WalletEntity(userId));

        assertTrue(walletRepository.findByIdAndUserIdForUpdate(wallet.getId(), userId).isPresent());
        assertFalse(walletRepository.findByIdAndUserIdForUpdate(wallet.getId(), UUID.randomUUID()).isPresent());

        Instant completedAt = Instant.parse("2026-10-04T12:00:00Z");
        DepositEntity deposit = depositRepository.saveAndFlush(new DepositEntity(
                wallet.getId(),
                userId,
                new BigDecimal("25.00"),
                WalletCurrency.RON,
                completedAt
        ));

        DepositEntity persistedDeposit = depositRepository.findById(deposit.getId()).orElseThrow();
        assertEquals(wallet.getId(), persistedDeposit.getWalletId());
        assertEquals(userId, persistedDeposit.getUserId());
        assertEquals(new BigDecimal("25.00"), persistedDeposit.getAmount());
        assertEquals(WalletCurrency.RON, persistedDeposit.getCurrency());
        assertEquals(completedAt, persistedDeposit.getCompletedAt());
        assertEquals(
                deposit.getId(),
                depositRepository.findAllByUserIdOrderByCompletedAtDesc(userId).getFirst().getId()
        );
        assertTrue(depositRepository.findAllByUserIdOrderByCompletedAtDesc(UUID.randomUUID()).isEmpty());
    }
}
