package com.walletx.walletservice.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WalletEntityTests {

    @Test
    void initializesWalletWithRonAndZeroBalance() {
        UUID userId = UUID.randomUUID();

        WalletEntity wallet = new WalletEntity(userId);

        assertNotNull(wallet.getId());
        assertEquals(userId, wallet.getUserId());
        assertEquals(WalletCurrency.RON, wallet.getCurrency());
        assertEquals(new BigDecimal("0.00"), wallet.getBalance());
    }

    @Test
    void rejectsNullOwner() {
        assertThrows(NullPointerException.class, () -> new WalletEntity(null));
    }
}
