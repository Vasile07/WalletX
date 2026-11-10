package com.walletx.transferservice.messaging;

import com.walletx.transferservice.domain.Transfer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TransferKafkaPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransferKafkaPublisher.class);

    private final KafkaTemplate<String, TransferKafkaMessage> kafkaTemplate;

    public TransferKafkaPublisher(KafkaTemplate<String, TransferKafkaMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishTransferCompleted(Transfer transfer) {
        TransferKafkaMessage event = new TransferKafkaMessage(
                UUID.randomUUID().toString(),
                "transfer.completed",
                1,
                transfer.getId(),
                transfer.getSenderWalletId(),
                transfer.getReceiverWalletId(),
                transfer.getAmount(),
                transfer.getCurrency(),
                transfer.getCompletedAt()
        );

        ProducerRecord<String, TransferKafkaMessage> record = new ProducerRecord<>(
                TransferKafkaConfig.TRANSFER_EVENT_TOPIC,
                transfer.getId().toString(),
                event
        );

        kafkaTemplate.send(record)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        LOGGER.error(
                                "Failed to publish transfer event {} to Kafka topic {}",
                                event.getTransferId(),
                                TransferKafkaConfig.TRANSFER_EVENT_TOPIC,
                                throwable
                        );
                        return;
                    }

                    LOGGER.info(
                            "Published transfer event {} to Kafka topic {} at offset {}",
                            event.getTransferId(),
                            TransferKafkaConfig.TRANSFER_EVENT_TOPIC,
                            result.getRecordMetadata().offset()
                    );
                });
    }
}
