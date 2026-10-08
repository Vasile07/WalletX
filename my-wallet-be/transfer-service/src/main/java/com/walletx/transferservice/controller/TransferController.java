package com.walletx.transferservice.controller;

import com.walletx.transferservice.business.TransferService;
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

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @GetMapping("/transfers")
    public List<TransferResponse> getTransfers(
            Authentication authentication,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
    ) {
        UUID currentUserId = resolveCurrentUserId(authentication);
        return transferService.findTransfersForUser(currentUserId, authorizationHeader).stream()
                .map(TransferResponse::fromDomain)
                .toList();
    }

    @PostMapping("/transfers")
    public ResponseEntity<TransferResponse> createTransfer(
            @Valid @RequestBody TransferRequest request,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
    ) {
        Transfer createdTransfer = transferService.createTransfer(request, authorizationHeader);
        return ResponseEntity.status(HttpStatus.CREATED).body(TransferResponse.fromDomain(createdTransfer));
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
