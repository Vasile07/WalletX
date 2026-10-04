package com.walletx.walletservice.controller;

import com.walletx.walletservice.domain.WalletEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class WalletResponse {

    private final UUID id;
    private final UUID userId;
    private final String currency;
    private final BigDecimal balance;

    public static WalletResponse fromEntity(WalletEntity wallet) {
        return new WalletResponse(
                wallet.getId(),
                wallet.getUserId(),
                wallet.getCurrency().name(),
                wallet.getBalance()
        );
    }
}
