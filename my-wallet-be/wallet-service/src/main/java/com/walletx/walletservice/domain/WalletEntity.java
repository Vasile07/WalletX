package com.walletx.walletservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "wallets")
@Getter
public class WalletEntity {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private WalletCurrency currency;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    protected WalletEntity() {
    }

    public WalletEntity(UUID userId) {
        this.id = UUID.randomUUID();
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.currency = WalletCurrency.RON;
        this.balance = new BigDecimal("0.00");
    }
}
