package com.walletx.apigateway.notification;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class LowBalanceNotificationRequest {

    private UUID id;
    private UUID userId;
    private UUID walletId;
    private String currency;
    private BigDecimal balance;
    private BigDecimal threshold;
    private String message;
    private Instant createdAt;
}
