package com.walletx.apigateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "walletx.jwt.secret=walletx-test-signing-secret-must-be-at-least-32-bytes"
})
@AutoConfigureWebTestClient
class GatewayApplicationTests {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void actuatorHealthEndpointIsAvailable() {
        webTestClient.get()
                .uri("/actuator/health")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void protectedRoutesRequireAuthentication() {
        webTestClient.get()
                .uri("/wallets")
                .exchange()
                .expectStatus().isUnauthorized();
    }
}
