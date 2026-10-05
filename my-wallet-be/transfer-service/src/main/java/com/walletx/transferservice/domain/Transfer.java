package com.walletx.transferservice.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class Transfer {

    private final UUID id;
    private final UUID senderWalletId;
    private final UUID receiverWalletId;
    private final BigDecimal amount;
    private final String currency;
    private final Instant completedAt;
}
