package com.walletx.walletservice.business;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.walletx.walletservice.domain.WalletCurrency;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class LowBalanceNotificationClientTests {

    @Test
    void postsNotificationPayloadToGatewayWithInternalToken() {
        RestClient.Builder restClientBuilder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restClientBuilder).build();
        LowBalanceNotificationClient client = new LowBalanceNotificationClient(
                restClientBuilder,
                "http://gateway.test",
                "internal-test-token"
        );
        UUID userId = UUID.randomUUID();
        UUID walletId = UUID.randomUUID();
        LowBalanceNotification notification = LowBalanceNotification.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .walletId(walletId)
                .currency(WalletCurrency.RON)
                .balance(new BigDecimal("95.00"))
                .threshold(new BigDecimal("100.00"))
                .message("Wallet balance is below the configured threshold")
                .createdAt(Instant.parse("2026-11-10T12:00:00Z"))
                .build();

        server.expect(requestTo("http://gateway.test/notifications/internal/low-balance"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-WalletX-Internal-Token", "internal-test-token"))
                .andExpect(content().json("""
                        {
                          "userId": "%s",
                          "walletId": "%s",
                          "currency": "RON",
                          "balance": 95.00,
                          "threshold": 100.00,
                          "message": "Wallet balance is below the configured threshold"
                        }
                        """.formatted(userId, walletId)))
                .andRespond(withSuccess());

        client.publish(notification);

        server.verify();
    }
}
