package com.walletx.transferservice.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransferEventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransferEventConsumer.class);

    @KafkaListener(
            topics = TransferKafkaConfig.TRANSFER_EVENT_TOPIC,
            groupId = "walletx-transfer-audit"
    )
    public void handleTransferCompleted(TransferKafkaMessage event) {
        LOGGER.info(
                "Received transfer event {} for transfer {}: {} -> {} [{} {}]",
                event.getEventType(),
                event.getTransferId(),
                event.getSenderWalletId(),
                event.getReceiverWalletId(),
                event.getAmount(),
                event.getCurrency()
        );
    }
}
