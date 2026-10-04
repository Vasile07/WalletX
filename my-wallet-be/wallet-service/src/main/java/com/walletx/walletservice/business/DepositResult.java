package com.walletx.walletservice.business;

import com.walletx.walletservice.domain.WalletEntity;
import com.walletx.walletservice.event.DepositCompletedEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DepositResult {

    private final WalletEntity wallet;
    private final DepositCompletedEvent event;
}
