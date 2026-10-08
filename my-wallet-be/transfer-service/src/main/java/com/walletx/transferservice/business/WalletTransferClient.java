package com.walletx.transferservice.business;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface WalletTransferClient {

    void executeTransfer(
            UUID senderWalletId,
            UUID receiverWalletId,
            BigDecimal amount,
            String currency,
            String authorizationHeader
    );

    List<UUID> findWalletIdsForUser(UUID userId, String authorizationHeader);
}
