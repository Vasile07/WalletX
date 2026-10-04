package com.walletx.walletservice.persistence;

import com.walletx.walletservice.domain.WalletCurrency;
import com.walletx.walletservice.domain.WalletEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class WalletRepositoryTests {

    @Autowired
    private WalletRepository walletRepository;

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
}
