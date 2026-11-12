package com.walletx.apigateway;

import static org.assertj.core.api.Assertions.assertThat;

import com.walletx.apigateway.notification.NotificationMessage;
import com.walletx.apigateway.notification.NotificationRegistry;
import com.walletx.apigateway.notification.NotificationSubscriber;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "walletx.jwt.secret=walletx-test-signing-secret-must-be-at-least-32-bytes",
        "walletx.notifications.internal-token=low-balance-test-token"
})
@AutoConfigureWebTestClient
class GatewayNotificationStreamTests {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private NotificationRegistry notificationRegistry;

    @Test
    void notificationsEndpointRequiresAuthentication() {
        webTestClient.get()
                .uri("/notifications")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void authenticatedClientCanOpenSseConnection() {
        String token = Jwts.builder()
                .subject("11111111-1111-4111-8111-111111111111")
                .claim("email", "alice@example.com")
                .claim("name", "Alice")
                .signWith(Keys.hmacShaKeyFor(
                        "walletx-test-signing-secret-must-be-at-least-32-bytes".getBytes(StandardCharsets.UTF_8)))
                .compact();

        String response = webTestClient.get()
                .uri("/notifications")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM)
                .returnResult(String.class)
                .getResponseBody()
                .blockFirst();

        assertThat(response).contains("connected");
    }

    @Test
    void publishesLowBalanceNotificationsToTheMatchingUserStream() {
        UUID userId = UUID.randomUUID();
        UUID walletId = UUID.randomUUID();
        AtomicReference<NotificationMessage> received = new AtomicReference<>();
        NotificationSubscriber subscriber = received::set;
        notificationRegistry.subscribe(userId, subscriber);
        try {
            webTestClient.post()
                    .uri("/notifications/internal/low-balance")
                    .header("X-WalletX-Internal-Token", "low-balance-test-token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue("""
                            {
                              "id": "%s",
                              "userId": "%s",
                              "walletId": "%s",
                              "currency": "RON",
                              "balance": 95.00,
                              "threshold": 100.00,
                              "message": "Wallet balance is below the configured threshold",
                              "createdAt": "%s"
                            }
                            """.formatted(UUID.randomUUID(), userId, walletId, Instant.now()))
                    .exchange()
                    .expectStatus().isAccepted()
                    .expectBody()
                    .jsonPath("$.type").isEqualTo("LOW_BALANCE")
                    .jsonPath("$.userId").isEqualTo(userId.toString())
                    .jsonPath("$.walletId").isEqualTo(walletId.toString())
                    .jsonPath("$.balance").isEqualTo(95.00)
                    .jsonPath("$.threshold").isEqualTo(100.00);

            assertThat(received.get()).isNotNull();
            assertThat(received.get().getType().name()).isEqualTo("LOW_BALANCE");
            assertThat(received.get().getUserId()).isEqualTo(userId);
        } finally {
            notificationRegistry.unsubscribe(userId, subscriber);
        }
    }

    @Test
    void internalLowBalanceEndpointRejectsInvalidServiceToken() {
        webTestClient.post()
                .uri("/notifications/internal/low-balance")
                .header("X-WalletX-Internal-Token", "wrong-token")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}")
                .exchange()
                .expectStatus().isUnauthorized();
    }
}
