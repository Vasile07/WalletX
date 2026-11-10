package com.walletx.transferservice.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class TransferMessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransferMessageListener.class);

    @RabbitListener(queues = TransferRabbitConfig.TRANSFER_COMPLETED_QUEUE)
    public void onTransferCompleted(TransferMessage message) {
        if (message == null || message.getTransferId() == null || message.getAmount() == null || message.getCurrency() == null) {
            throw new IllegalArgumentException("Invalid transfer completion message");
        }

        LOGGER.info(
                "Transfer {} completed for wallet {} to wallet {} with amount {} {}",
                message.getTransferId(),
                message.getSenderWalletId(),
                message.getReceiverWalletId(),
                message.getAmount(),
                message.getCurrency()
        );
    }
}
