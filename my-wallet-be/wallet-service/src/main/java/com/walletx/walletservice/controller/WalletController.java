package com.walletx.walletservice.controller;

import com.walletx.walletservice.business.WalletService;
import com.walletx.walletservice.domain.WalletEntity;
import com.walletx.walletservice.security.CurrentUserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping({"/api", ""})
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/wallets")
    public List<WalletResponse> getWallets(Authentication authentication) {
        UUID currentUserId = resolveCurrentUserId(authentication);
        return walletService.findAllByUserId(currentUserId).stream()
                .map(WalletResponse::fromEntity)
                .toList();
    }

    @PostMapping("/wallets")
    public ResponseEntity<WalletResponse> createWallet(Authentication authentication) {
        UUID currentUserId = resolveCurrentUserId(authentication);
        WalletEntity createdWallet = walletService.createWallet(currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(WalletResponse.fromEntity(createdWallet));
    }

    private UUID resolveCurrentUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Authentication required");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CurrentUserPrincipal currentUser) {
            return currentUser.getId();
        }
        if (principal instanceof UUID userId) {
            return userId;
        }
        if (principal instanceof String userIdString) {
            return UUID.fromString(userIdString);
        }

        throw new AccessDeniedException("Unable to resolve authenticated user");
    }
}
