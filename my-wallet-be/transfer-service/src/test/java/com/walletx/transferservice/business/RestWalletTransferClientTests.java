package com.walletx.transferservice.business;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

class RestWalletTransferClientTests {

    @Test
    void forwardsAuthenticatedTransferToWalletService() {
        RestClient.Builder restClientBuilder = RestClient.builder()
                .messageConverters(converters -> converters.add(new MappingJackson2HttpMessageConverter()));
        MockRestServiceServer server = MockRestServiceServer.bindTo(restClientBuilder).build();
        RestWalletTransferClient client = new RestWalletTransferClient(
                restClientBuilder, "http://wallet-service", "internal-test-token");
        UUID senderWalletId = UUID.randomUUID();
        UUID receiverWalletId = UUID.randomUUID();

        server.expect(requestTo("http://wallet-service/api/internal/transfers"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Bearer " + "user-test-token"))
                .andExpect(header("X-Internal-Transfer-Token", "internal-test-token"))
                .andExpect(jsonPath("$.senderWalletId").value(senderWalletId.toString()))
                .andExpect(jsonPath("$.receiverWalletId").value(receiverWalletId.toString()))
                .andExpect(jsonPath("$.amount").value(12.5))
                .andExpect(jsonPath("$.currency").value("RON"))
                .andRespond(withStatus(HttpStatus.NO_CONTENT));

        client.executeTransfer(
                senderWalletId,
                receiverWalletId,
                new BigDecimal("12.50"),
                "RON",
                "Bearer " + "user-test-token");

        server.verify();
    }

    @Test
    void fetchesWalletIdsForAuthenticatedUser() {
        RestClient.Builder restClientBuilder = RestClient.builder()
                .messageConverters(converters -> converters.add(new MappingJackson2HttpMessageConverter()));
        MockRestServiceServer server = MockRestServiceServer.bindTo(restClientBuilder).build();
        RestWalletTransferClient client = new RestWalletTransferClient(
                restClientBuilder, "http://wallet-service", "internal-test-token");
        UUID firstWalletId = UUID.randomUUID();
        UUID secondWalletId = UUID.randomUUID();

        server.expect(requestTo("http://wallet-service/api/wallets"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer user-test-token"))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("[{\"id\":\"" + firstWalletId + "\"},{\"id\":\"" + secondWalletId + "\"}]"));

        java.util.List<UUID> walletIds = client.findWalletIdsForUser(UUID.randomUUID(), "Bearer user-test-token");

        org.junit.jupiter.api.Assertions.assertEquals(java.util.List.of(firstWalletId, secondWalletId), walletIds);
        server.verify();
    }
}
