package com.walletx.walletservice.business;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.client.RestClientException;

@Component
@RequiredArgsConstructor
public class LowBalanceNotificationDelivery {

    private static final Logger LOGGER = LoggerFactory.getLogger(LowBalanceNotificationDelivery.class);

    private final LowBalanceNotificationClient notificationClient;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void deliver(LowBalanceNotification notification) {
        try {
            notificationClient.publish(notification);
        } catch (RestClientException | IllegalStateException exception) {
            LOGGER.error(
                    "Wallet balance committed, but low-balance notification delivery failed for wallet {}",
                    notification.getWalletId(),
                    exception
            );
        }
    }
}
