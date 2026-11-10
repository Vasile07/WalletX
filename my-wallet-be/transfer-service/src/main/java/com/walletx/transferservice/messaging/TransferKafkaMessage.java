package com.walletx.transferservice.messaging;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class TransferKafkaMessage extends TransferMessage {

    private String eventId;
    private String eventType;
    private int eventVersion;

    public TransferKafkaMessage(
            String eventId,
            String eventType,
            int eventVersion,
            UUID transferId,
            UUID senderWalletId,
            UUID receiverWalletId,
            BigDecimal amount,
            String currency,
            Instant completedAt
    ) {
        super(transferId, senderWalletId, receiverWalletId, amount, currency, completedAt);
        this.eventId = eventId;
        this.eventType = eventType;
        this.eventVersion = eventVersion;
    }
}
