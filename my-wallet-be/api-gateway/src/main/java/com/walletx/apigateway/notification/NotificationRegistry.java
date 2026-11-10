package com.walletx.apigateway.notification;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import org.springframework.stereotype.Component;

@Component
public class NotificationRegistry {

    private final Map<UUID, Set<NotificationSubscriber>> subscribers = new ConcurrentHashMap<>();

    public void subscribe(UUID userId, NotificationSubscriber subscriber) {
        subscribers.computeIfAbsent(userId, ignored -> new CopyOnWriteArraySet<>())
                .add(subscriber);
    }

    public void unsubscribe(UUID userId, NotificationSubscriber subscriber) {
        Set<NotificationSubscriber> userSubscribers = subscribers.get(userId);
        if (userSubscribers == null) {
            return;
        }

        userSubscribers.remove(subscriber);
        if (userSubscribers.isEmpty()) {
            subscribers.remove(userId, userSubscribers);
        }
    }

    public void publish(NotificationMessage message) {
        if (message == null || message.getUserId() == null) {
            return;
        }

        Set<NotificationSubscriber> userSubscribers = subscribers.get(message.getUserId());
        if (userSubscribers == null || userSubscribers.isEmpty()) {
            return;
        }

        for (NotificationSubscriber subscriber : userSubscribers) {
            subscriber.accept(message);
        }
    }
}
