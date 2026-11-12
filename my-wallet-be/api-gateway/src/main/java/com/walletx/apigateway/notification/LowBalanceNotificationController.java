package com.walletx.apigateway.notification;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/notifications/internal")
public class LowBalanceNotificationController {

    private static final String INTERNAL_TOKEN_HEADER = "X-WalletX-Internal-Token";

    private final NotificationService notificationService;
    private final String internalToken;

    public LowBalanceNotificationController(
            NotificationService notificationService,
            @Value("${walletx.notifications.internal-token:}") String internalToken
    ) {
        this.notificationService = notificationService;
        this.internalToken = internalToken;
    }

    @PostMapping("/low-balance")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public NotificationMessage publishLowBalanceNotification(
            @RequestHeader(value = INTERNAL_TOKEN_HEADER, required = false) String suppliedToken,
            @RequestBody LowBalanceNotificationRequest request
    ) {
        verifyInternalToken(suppliedToken);
        if (request == null
                || request.getUserId() == null
                || request.getWalletId() == null
                || request.getBalance() == null
                || request.getThreshold() == null
                || request.getCurrency() == null
                || request.getMessage() == null
                || request.getMessage().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid low-balance notification");
        }

        return notificationService.publishLowBalance(request);
    }

    private void verifyInternalToken(String suppliedToken) {
        if (internalToken == null
                || internalToken.isBlank()
                || suppliedToken == null
                || !MessageDigest.isEqual(
                        internalToken.getBytes(StandardCharsets.UTF_8),
                        suppliedToken.getBytes(StandardCharsets.UTF_8))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid internal service token");
        }
    }
}
