package com.walletx.apigateway.notification;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationMessage {

    private UUID id;
    private UUID userId;
    private NotificationType type;
    private String message;
    private UUID walletId;
    private UUID transferId;
    private UUID depositId;
    private Instant createdAt;
    private boolean connected;

    public static NotificationMessage connected(UUID userId) {
        return NotificationMessage.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .type(NotificationType.SYSTEM)
                .message("Connected to WalletX notifications")
                .createdAt(Instant.now())
                .connected(true)
                .build();
    }
}
