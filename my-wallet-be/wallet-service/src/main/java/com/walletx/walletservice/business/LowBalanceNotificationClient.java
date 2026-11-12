package com.walletx.walletservice.business;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class LowBalanceNotificationClient {

    private static final String INTERNAL_TOKEN_HEADER = "X-WalletX-Internal-Token";

    private final RestClient restClient;
    private final String internalToken;

    public LowBalanceNotificationClient(
            RestClient.Builder restClientBuilder,
            @Value("${walletx.notifications.gateway-url:http://localhost:8080}") String gatewayUrl,
            @Value("${walletx.notifications.internal-token:}") String internalToken
    ) {
        this.restClient = restClientBuilder.baseUrl(gatewayUrl).build();
        this.internalToken = internalToken;
    }

    public void publish(LowBalanceNotification notification) {
        if (internalToken == null || internalToken.isBlank()) {
            throw new IllegalStateException(
                    "Low-balance notification token must be configured using WALLET_LOW_BALANCE_NOTIFICATION_TOKEN");
        }

        restClient.post()
                .uri("/notifications/internal/low-balance")
                .header(INTERNAL_TOKEN_HEADER, internalToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(notification)
                .retrieve()
                .toBodilessEntity();
    }
}
