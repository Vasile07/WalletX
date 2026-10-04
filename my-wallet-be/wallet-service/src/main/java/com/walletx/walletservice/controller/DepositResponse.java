package com.walletx.walletservice.controller;

import com.walletx.walletservice.business.DepositResult;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class DepositResponse {

    private final UUID depositId;
    private final UUID walletId;
    private final BigDecimal amount;
    private final String currency;
    private final BigDecimal balance;
    private final Instant completedAt;

    public static DepositResponse fromResult(DepositResult result) {
        return new DepositResponse(
                result.getEvent().getDepositId(),
                result.getEvent().getWalletId(),
                result.getEvent().getAmount(),
                result.getEvent().getCurrency().name(),
                result.getWallet().getBalance(),
                result.getEvent().getOccurredAt()
        );
    }
}
