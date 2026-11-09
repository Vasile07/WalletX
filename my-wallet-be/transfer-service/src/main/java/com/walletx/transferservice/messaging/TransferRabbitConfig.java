package com.walletx.transferservice.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TransferRabbitConfig {

    public static final String TRANSFER_EXCHANGE = "walletx.transfer.exchange";
    public static final String TRANSFER_ROUTING_KEY = "transfer.completed";
    public static final String TRANSFER_COMPLETED_QUEUE = "walletx.transfer.completed.queue";
    public static final String TRANSFER_RETRY_EXCHANGE = "walletx.transfer.retry.exchange";
    public static final String TRANSFER_RETRY_QUEUE = "walletx.transfer.retry.queue";
    public static final String TRANSFER_DEAD_LETTER_EXCHANGE = "walletx.transfer.dead-letter.exchange";
    public static final String TRANSFER_DEAD_LETTER_QUEUE = "walletx.transfer.dead-letter.queue";
    public static final String TRANSFER_DEAD_LETTER_ROUTING_KEY = "transfer.failed";

    @Bean
    public TopicExchange transferExchange() {
        return new TopicExchange(TRANSFER_EXCHANGE, true, false);
    }

    @Bean
    public Queue transferCompletedQueue() {
        return QueueBuilder.durable(TRANSFER_COMPLETED_QUEUE)
                .withArgument("x-dead-letter-exchange", TRANSFER_RETRY_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", TRANSFER_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding transferCompletedBinding() {
        return BindingBuilder.bind(transferCompletedQueue())
                .to(transferExchange())
                .with(TRANSFER_ROUTING_KEY);
    }

    @Bean
    public DirectExchange transferRetryExchange() {
        return new DirectExchange(TRANSFER_RETRY_EXCHANGE, true, false);
    }

    @Bean
    public Queue transferRetryQueue() {
        return QueueBuilder.durable(TRANSFER_RETRY_QUEUE)
                .withArgument("x-message-ttl", 5_000)
                .withArgument("x-dead-letter-exchange", TRANSFER_DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", TRANSFER_DEAD_LETTER_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding transferRetryBinding() {
        return BindingBuilder.bind(transferRetryQueue())
                .to(transferRetryExchange())
                .with(TRANSFER_ROUTING_KEY);
    }

    @Bean
    public DirectExchange transferDeadLetterExchange() {
        return new DirectExchange(TRANSFER_DEAD_LETTER_EXCHANGE, true, false);
    }

    @Bean
    public Queue transferDeadLetterQueue() {
        return QueueBuilder.durable(TRANSFER_DEAD_LETTER_QUEUE).build();
    }

    @Bean
    public Binding transferDeadLetterBinding() {
        return BindingBuilder.bind(transferDeadLetterQueue())
                .to(transferDeadLetterExchange())
                .with(TRANSFER_DEAD_LETTER_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
