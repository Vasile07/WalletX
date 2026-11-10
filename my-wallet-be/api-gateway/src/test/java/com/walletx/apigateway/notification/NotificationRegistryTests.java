package com.walletx.apigateway.notification;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class NotificationRegistryTests {

    @Test
    void publishesOnlyToMatchingUser() {
        NotificationRegistry registry = new NotificationRegistry();
        UUID aliceId = UUID.fromString("11111111-1111-4111-8111-111111111111");
        UUID bobId = UUID.fromString("22222222-2222-4222-8222-222222222222");

        NotificationMessage[] aliceMessages = new NotificationMessage[1];
        NotificationMessage[] bobMessages = new NotificationMessage[1];

        registry.subscribe(aliceId, message -> aliceMessages[0] = message);
        registry.subscribe(bobId, message -> bobMessages[0] = message);

        NotificationMessage transfer = NotificationMessage.builder()
                .id(UUID.randomUUID())
                .userId(aliceId)
                .type(NotificationType.TRANSFER_SENT)
                .message("Transfer sent")
                .createdAt(Instant.now())
                .build();

        registry.publish(transfer);

        assertThat(aliceMessages[0]).isEqualTo(transfer);
        assertThat(bobMessages[0]).isNull();
    }
}
