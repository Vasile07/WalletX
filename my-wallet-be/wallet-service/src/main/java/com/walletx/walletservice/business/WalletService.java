package com.walletx.walletservice.business;

import com.walletx.walletservice.domain.WalletEntity;

import java.util.List;
import java.util.UUID;

public interface WalletService {

    List<WalletEntity> findAllByUserId(UUID userId);

    WalletEntity createWallet(UUID userId);
}
