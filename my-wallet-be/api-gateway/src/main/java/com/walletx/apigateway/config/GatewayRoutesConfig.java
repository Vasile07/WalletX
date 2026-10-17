package com.walletx.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouteLocator walletxRoutes(
            RouteLocatorBuilder builder,
            @Value("${walletx.services.user.base-url:http://localhost:8081}") String userServiceUrl,
            @Value("${walletx.services.wallet.base-url:http://localhost:8082}") String walletServiceUrl,
            @Value("${walletx.services.transfer.base-url:http://localhost:8083}") String transferServiceUrl
    ) {
        return builder.routes()
                .route("user-service", route -> route
                        .path("/auth/**", "/users/**")
                        .uri(userServiceUrl))
                .route("wallet-service", route -> route
                        .path("/wallets/**")
                        .uri(walletServiceUrl))
                .route("transfer-service", route -> route
                        .path("/transfers/**")
                        .uri(transferServiceUrl))
                .build();
    }
}
