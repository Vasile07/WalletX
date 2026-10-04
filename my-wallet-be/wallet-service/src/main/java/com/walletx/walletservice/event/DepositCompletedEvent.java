package com.walletx.walletservice.event;

import com.walletx.walletservice.domain.WalletCurrency;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class DepositCompletedEvent {

    private final UUID depositId;
    private final UUID walletId;
    private final UUID userId;
    private final BigDecimal amount;
    private final WalletCurrency currency;
    private final Instant occurredAt;
}
