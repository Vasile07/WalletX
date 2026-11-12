package com.walletx.apigateway.notification;

import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRegistry notificationRegistry;

    public NotificationMessage publishForUser(
            UUID userId,
            NotificationType type,
            String message,
            UUID walletId,
            UUID transferId,
            UUID depositId
    ) {
        NotificationMessage notification = NotificationMessage.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .type(type)
                .message(message)
                .walletId(walletId)
                .transferId(transferId)
                .depositId(depositId)
                .createdAt(Instant.now())
                .build();

        notificationRegistry.publish(notification);
        return notification;
    }

    public NotificationMessage publishLowBalance(LowBalanceNotificationRequest request) {
        NotificationMessage notification = NotificationMessage.builder()
                .id(request.getId() == null ? UUID.randomUUID() : request.getId())
                .userId(request.getUserId())
                .type(NotificationType.LOW_BALANCE)
                .message(request.getMessage())
                .walletId(request.getWalletId())
                .currency(request.getCurrency())
                .balance(request.getBalance())
                .threshold(request.getThreshold())
                .createdAt(request.getCreatedAt() == null ? Instant.now() : request.getCreatedAt())
                .build();

        notificationRegistry.publish(notification);
        return notification;
    }
}
