package com.walletx.walletservice.controller;

import com.walletx.walletservice.domain.WalletCurrency;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class WalletTransferRequest {

    @NotNull
    private UUID senderWalletId;

    @NotNull
    private UUID receiverWalletId;

    @NotNull
    @DecimalMin(value = "0.01")
    @Digits(integer = 17, fraction = 2)
    private BigDecimal amount;

    @NotNull
    private WalletCurrency currency;
}
