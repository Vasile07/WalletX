package com.walletx.transferservice.controller;

import com.walletx.transferservice.domain.Transfer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class TransferResponse {

    private final UUID id;
    private final UUID senderWalletId;
    private final UUID receiverWalletId;
    private final BigDecimal amount;
    private final String currency;
    private final Instant completedAt;

    public static TransferResponse fromDomain(Transfer transfer) {
        return new TransferResponse(
                transfer.getId(),
                transfer.getSenderWalletId(),
                transfer.getReceiverWalletId(),
                transfer.getAmount(),
                transfer.getCurrency(),
                transfer.getCompletedAt()
        );
    }
}
