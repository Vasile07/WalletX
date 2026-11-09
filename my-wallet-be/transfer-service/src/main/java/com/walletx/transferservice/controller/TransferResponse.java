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
    private final String type;
    private final String direction;
    private final UUID senderWalletId;
    private final UUID receiverWalletId;
    private final UUID counterpartyWalletId;
    private final BigDecimal amount;
    private final String currency;
    private final Instant completedAt;

    public static TransferResponse fromDomain(Transfer transfer, UUID currentUserId, java.util.List<UUID> ownedWalletIds) {
        boolean sentByCurrentUser = ownedWalletIds != null && ownedWalletIds.contains(transfer.getSenderWalletId());
        boolean receivedByCurrentUser = ownedWalletIds != null && ownedWalletIds.contains(transfer.getReceiverWalletId());
        String direction = sentByCurrentUser ? "SENT" : receivedByCurrentUser ? "RECEIVED" : "UNKNOWN";
        UUID counterpartyWalletId = sentByCurrentUser ? transfer.getReceiverWalletId() : transfer.getSenderWalletId();

        return new TransferResponse(
                transfer.getId(),
                "TRANSFER",
                direction,
                transfer.getSenderWalletId(),
                transfer.getReceiverWalletId(),
                counterpartyWalletId,
                transfer.getAmount(),
                transfer.getCurrency(),
                transfer.getCompletedAt()
        );
    }
}
