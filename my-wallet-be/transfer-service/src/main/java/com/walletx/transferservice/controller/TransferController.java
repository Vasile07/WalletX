package com.walletx.transferservice.controller;

import com.walletx.transferservice.business.TransferService;
import com.walletx.transferservice.business.WalletTransferClient;
import com.walletx.transferservice.domain.Transfer;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping({"/api", ""})
public class TransferController {

    private final TransferService transferService;
    private final WalletTransferClient walletTransferClient;

    public TransferController(TransferService transferService, WalletTransferClient walletTransferClient) {
        this.transferService = transferService;
        this.walletTransferClient = walletTransferClient;
    }

    @GetMapping("/transfers")
    public List<TransferResponse> getTransfers(
            Authentication authentication,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
    ) {
        UUID currentUserId = resolveCurrentUserId(authentication);
        List<UUID> ownedWalletIds = walletTransferClient.findWalletIdsForUser(currentUserId, authorizationHeader);
        return transferService.findTransfersForUser(currentUserId, authorizationHeader).stream()
                .map(transfer -> TransferResponse.fromDomain(transfer, currentUserId, ownedWalletIds))
                .toList();
    }

    @PostMapping("/transfers")
    public ResponseEntity<TransferResponse> createTransfer(
            @Valid @RequestBody TransferRequest request,
            Authentication authentication,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
    ) {
        UUID currentUserId = resolveCurrentUserId(authentication);
        List<UUID> ownedWalletIds = walletTransferClient.findWalletIdsForUser(currentUserId, authorizationHeader);
        Transfer createdTransfer = transferService.createTransfer(request, authorizationHeader);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TransferResponse.fromDomain(createdTransfer, currentUserId, ownedWalletIds));
    }

    private UUID resolveCurrentUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Authentication required");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UUID userId) {
            return userId;
        }
        if (principal instanceof String userIdString) {
            return UUID.fromString(userIdString);
        }

        throw new AccessDeniedException("Unable to resolve authenticated user");
    }
}
