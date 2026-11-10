package com.walletx.transferservice.messaging;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@EnableKafka
public class TransferKafkaConfig {

    public static final String TRANSFER_EVENT_TOPIC = "money-transfers";

    @Bean
    public NewTopic moneyTransfersTopic() {
        return TopicBuilder.name(TRANSFER_EVENT_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
