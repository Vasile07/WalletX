package com.walletx.apigateway.notification;

@FunctionalInterface
public interface NotificationSubscriber {
    
    void accept(NotificationMessage message);
}
