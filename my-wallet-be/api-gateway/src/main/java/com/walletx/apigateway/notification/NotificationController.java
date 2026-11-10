package com.walletx.apigateway.notification;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationRegistry notificationRegistry;

    public NotificationController(NotificationRegistry notificationRegistry) {
        this.notificationRegistry = notificationRegistry;
    }

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<NotificationMessage>> streamNotifications(
            @RequestHeader(value = "X-User-Id", required = false) String userId
    ) {
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing authenticated user id");
        }

        UUID currentUserId = UUID.fromString(userId);

        return Flux.create(sink -> {
            NotificationSubscriber subscriber = notification -> sink.next(ServerSentEvent.<NotificationMessage>builder()
                    .event(notification.getType().name().toLowerCase(Locale.ROOT))
                    .id(notification.getId().toString())
                    .data(notification)
                    .build());

            notificationRegistry.subscribe(currentUserId, subscriber);
            sink.onCancel(() -> notificationRegistry.unsubscribe(currentUserId, subscriber));
            sink.onDispose(() -> notificationRegistry.unsubscribe(currentUserId, subscriber));

            sink.next(ServerSentEvent.<NotificationMessage>builder()
                    .event("connected")
                    .id("connected")
                    .data(NotificationMessage.connected(currentUserId))
                    .build());
        });
    }

    @PostMapping("/publish")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public NotificationMessage publishNotification(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestBody NotificationMessage notification
    ) {
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing authenticated user id");
        }

        UUID currentUserId = UUID.fromString(userId);
        NotificationMessage event = notification == null ? new NotificationMessage() : notification;
        if (event.getUserId() == null) {
            event.setUserId(currentUserId);
        }
        if (event.getType() == null) {
            event.setType(NotificationType.SYSTEM);
        }
        if (event.getMessage() == null || event.getMessage().isBlank()) {
            event.setMessage("WalletX notification");
        }
        if (event.getId() == null) {
            event.setId(UUID.randomUUID());
        }
        if (event.getCreatedAt() == null) {
            event.setCreatedAt(Instant.now());
        }

        notificationRegistry.publish(event);
        return event;
    }
}
