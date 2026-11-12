package com.walletx.walletservice.business;

import com.walletx.walletservice.domain.WalletCurrency;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LowBalanceFunction {

    public static final BigDecimal DEFAULT_THRESHOLD = new BigDecimal("100.00");

    private final BigDecimal threshold;

    public LowBalanceFunction(
            @Value("${walletx.low-balance-threshold:100.00}") String thresholdValue
    ) {
        this.threshold = new BigDecimal(thresholdValue).setScale(2);
    }

    public Optional<LowBalanceNotification> evaluate(
            UUID userId,
            UUID walletId,
            BigDecimal previousBalance,
            BigDecimal currentBalance,
            WalletCurrency currency
    ) {
        if (userId == null || walletId == null || currentBalance == null || currency == null) {
            return Optional.empty();
        }
        if (currency != WalletCurrency.RON) {
            return Optional.empty();
        }
        if (threshold.compareTo(BigDecimal.ZERO) <= 0) {
            return Optional.empty();
        }

        BigDecimal previous = previousBalance == null ? BigDecimal.ZERO : previousBalance;
        boolean wasAboveOrEqualToThreshold = previous.compareTo(threshold) >= 0;
        boolean isBelowThreshold = currentBalance.compareTo(threshold) < 0;
        if (!wasAboveOrEqualToThreshold || !isBelowThreshold) {
            return Optional.empty();
        }

        String message = String.format(
                "Wallet balance is below the configured threshold (%s RON). Current balance: %s RON",
                threshold.setScale(2),
                currentBalance.setScale(2)
        );

        return Optional.of(LowBalanceNotification.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .walletId(walletId)
                .currency(currency)
                .balance(currentBalance.setScale(2))
                .threshold(threshold.setScale(2))
                .message(message)
                .createdAt(Instant.now())
                .build());
    }
}
