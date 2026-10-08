package com.walletx.transferservice.business;

import com.walletx.transferservice.controller.TransferRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class RestWalletTransferClient implements WalletTransferClient {

    private final RestClient restClient;
    private final String internalTransferToken;

    public RestWalletTransferClient(
            RestClient.Builder restClientBuilder,
            @Value("${walletx.services.wallet.base-url}") String walletServiceBaseUrl,
            @Value("${walletx.services.wallet.internal-transfer-token:}") String internalTransferToken
    ) {
        this.restClient = restClientBuilder.baseUrl(walletServiceBaseUrl).build();
        this.internalTransferToken = internalTransferToken;
    }

    @Override
    public void executeTransfer(
            UUID senderWalletId,
            UUID receiverWalletId,
            BigDecimal amount,
            String currency,
            String authorizationHeader
    ) {
        if (internalTransferToken.isBlank()) {
            throw new WalletServiceUnavailableException(
                    "Wallet Service internal transfer token is not configured",
                    new IllegalStateException("Configure WALLET_INTERNAL_TRANSFER_TOKEN"));
        }

        try {
            TransferRequest request = new TransferRequest();
            request.setSenderWalletId(senderWalletId);
            request.setReceiverWalletId(receiverWalletId);
            request.setAmount(amount);
            request.setCurrency(currency);
            restClient.post()
                    .uri("/api/internal/transfers")
                    .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                    .header("X-Internal-Transfer-Token", internalTransferToken)
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.BadRequest exception) {
            throw new InvalidTransferException("Wallet Service rejected the transfer request");
        } catch (HttpClientErrorException.Forbidden exception) {
            throw new AccessDeniedException(
                    "Sender wallet is not owned by the authenticated user");
        } catch (HttpClientErrorException.UnprocessableEntity exception) {
            throw new InsufficientFundsException("Sender wallet does not have sufficient funds");
        } catch (HttpClientErrorException.NotFound exception) {
            throw new InvalidTransferException("A wallet in the transfer was not found");
        } catch (ResourceAccessException exception) {
            throw new WalletServiceUnavailableException("Wallet Service could not be reached", exception);
        } catch (RestClientResponseException exception) {
            throw new WalletServiceUnavailableException(
                    "Wallet Service returned HTTP " + exception.getStatusCode().value(), exception);
        }
    }

    @Override
    public List<UUID> findWalletIdsForUser(UUID userId, String authorizationHeader) {
        if (userId == null) {
            throw new AccessDeniedException("Authentication required");
        }
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new AccessDeniedException("Authentication required");
        }

        try {
            WalletListEntry[] wallets = restClient.get()
                    .uri("/api/wallets")
                    .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                    .retrieve()
                    .body(WalletListEntry[].class);

            if (wallets == null || wallets.length == 0) {
                return List.of();
            }

            return Arrays.stream(wallets)
                    .filter(Objects::nonNull)
                    .map(WalletListEntry::getId)
                    .filter(Objects::nonNull)
                    .toList();
        } catch (HttpClientErrorException.Forbidden exception) {
            throw new AccessDeniedException("Authentication required");
        } catch (HttpClientErrorException.Unauthorized exception) {
            throw new AccessDeniedException("Authentication required");
        } catch (ResourceAccessException exception) {
            throw new WalletServiceUnavailableException("Wallet Service could not be reached", exception);
        } catch (RestClientResponseException exception) {
            throw new WalletServiceUnavailableException(
                    "Wallet Service returned HTTP " + exception.getStatusCode().value(), exception);
        }
    }

    private static final class WalletListEntry {
        private UUID id;

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }
    }
}

