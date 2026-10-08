package com.walletx.transferservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "transfers")
@Getter
public class Transfer {

    @Id
    private UUID id;

    @Column(name = "sender_wallet_id", nullable = false)
    private UUID senderWalletId;

    @Column(name = "receiver_wallet_id", nullable = false)
    private UUID receiverWalletId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "completed_at", nullable = false)
    private Instant completedAt;

    protected Transfer() {
    }

    public Transfer(UUID senderWalletId, UUID receiverWalletId, BigDecimal amount, String currency) {
        this.id = UUID.randomUUID();
        this.senderWalletId = Objects.requireNonNull(senderWalletId, "senderWalletId must not be null");
        this.receiverWalletId = Objects.requireNonNull(receiverWalletId, "receiverWalletId must not be null");
        this.amount = Objects.requireNonNull(amount, "amount must not be null");
        this.currency = Objects.requireNonNull(currency, "currency must not be null");
        this.completedAt = Instant.now();
    }
}
