package com.walletx.transferservice.messaging;

import com.walletx.transferservice.domain.Transfer;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class TransferMessagePublisher {

    private final RabbitTemplate rabbitTemplate;

    public TransferMessagePublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishTransferCompleted(Transfer transfer) {
        TransferMessage message = new TransferMessage(
                transfer.getId(),
                transfer.getSenderWalletId(),
                transfer.getReceiverWalletId(),
                transfer.getAmount(),
                transfer.getCurrency(),
                transfer.getCompletedAt()
        );

        rabbitTemplate.convertAndSend(
                TransferRabbitConfig.TRANSFER_EXCHANGE,
                TransferRabbitConfig.TRANSFER_ROUTING_KEY,
                message
        );
    }
}
