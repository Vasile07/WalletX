package com.walletx.walletservice.business;

import com.walletx.walletservice.domain.WalletCurrency;
import com.walletx.walletservice.domain.WalletEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface WalletService {

    List<WalletEntity> findAllByUserId(UUID userId);

    WalletEntity createWallet(UUID userId);

    DepositResult deposit(UUID currentUserId, UUID walletId, BigDecimal amount, WalletCurrency currency);
}
