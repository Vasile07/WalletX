package com.walletx.walletservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "wallet_deposits")
@Getter
public class DepositEntity {

    @Id
    private UUID id;

    @Column(name = "wallet_id", nullable = false)
    private UUID walletId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private WalletCurrency currency;

    @Column(name = "completed_at", nullable = false)
    private Instant completedAt;

    protected DepositEntity() {
    }

    public DepositEntity(
            UUID walletId,
            UUID userId,
            BigDecimal amount,
            WalletCurrency currency,
            Instant completedAt
    ) {
        this.id = UUID.randomUUID();
        this.walletId = Objects.requireNonNull(walletId, "walletId must not be null");
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.amount = Objects.requireNonNull(amount, "amount must not be null");
        this.currency = Objects.requireNonNull(currency, "currency must not be null");
        this.completedAt = Objects.requireNonNull(completedAt, "completedAt must not be null");
    }
}
