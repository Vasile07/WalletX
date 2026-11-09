package com.walletx.transferservice.messaging;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransferMessage(
        UUID transferId,
        UUID senderWalletId,
        UUID receiverWalletId,
        BigDecimal amount,
        String currency,
        Instant completedAt
) {
}
