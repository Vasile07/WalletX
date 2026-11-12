package com.walletx.walletservice.business;

import com.walletx.walletservice.domain.WalletCurrency;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class LowBalanceNotification {

    private final UUID id;
    private final UUID userId;
    private final UUID walletId;
    private final WalletCurrency currency;
    private final BigDecimal balance;
    private final BigDecimal threshold;
    private final String message;
    private final Instant createdAt;
}
