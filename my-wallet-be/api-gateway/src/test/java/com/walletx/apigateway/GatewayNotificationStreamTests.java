package com.walletx.apigateway;

import static org.assertj.core.api.Assertions.assertThat;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "walletx.jwt.secret=walletx-test-signing-secret-must-be-at-least-32-bytes"
})
@AutoConfigureWebTestClient
class GatewayNotificationStreamTests {

    @Autowired
    private WebTestClient webTestClient;

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
}
