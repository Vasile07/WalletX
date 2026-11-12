package com.walletx.walletservice.business;

import static org.assertj.core.api.Assertions.assertThat;

import com.walletx.walletservice.domain.WalletCurrency;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class LowBalanceFunctionTests {

    private final LowBalanceFunction function = new LowBalanceFunction("100.00");

    @Test
    void triggersOnceWhenBalanceCrossesBelowThreshold() {
        UUID userId = UUID.randomUUID();
        UUID walletId = UUID.randomUUID();

        Optional<LowBalanceNotification> notification = function.evaluate(
                userId,
                walletId,
                new BigDecimal("120.00"),
                new BigDecimal("99.99"),
                WalletCurrency.RON
        );

        assertThat(notification).isPresent();
        assertThat(notification.get().getUserId()).isEqualTo(userId);
        assertThat(notification.get().getWalletId()).isEqualTo(walletId);
        assertThat(notification.get().getThreshold()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(notification.get().getBalance()).isEqualByComparingTo(new BigDecimal("99.99"));
    }

    @Test
    void ignoresBalancesAlreadyBelowThreshold() {
        UUID userId = UUID.randomUUID();
        UUID walletId = UUID.randomUUID();

        Optional<LowBalanceNotification> notification = function.evaluate(
                userId,
                walletId,
                new BigDecimal("79.00"),
                new BigDecimal("60.00"),
                WalletCurrency.RON
        );

        assertThat(notification).isEmpty();
    }

    @Test
    void ignoresBalancesAboveThreshold() {
        Optional<LowBalanceNotification> notification = function.evaluate(
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("120.00"),
                new BigDecimal("101.00"),
                WalletCurrency.RON
        );

        assertThat(notification).isEmpty();
    }
}
