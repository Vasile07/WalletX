package com.walletx.transferservice.controller;

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
public class TransferRequest {

    @NotNull(message = "senderWalletId must not be null")
    private UUID senderWalletId;

    @NotNull(message = "receiverWalletId must not be null")
    private UUID receiverWalletId;

    @NotNull(message = "amount must not be null")
    @DecimalMin(value = "0.01", message = "amount must be greater than zero")
    @Digits(integer = 17, fraction = 2, message = "amount must have at most two decimal places")
    private BigDecimal amount;

    @NotNull(message = "currency must not be null")
    private String currency;
}
