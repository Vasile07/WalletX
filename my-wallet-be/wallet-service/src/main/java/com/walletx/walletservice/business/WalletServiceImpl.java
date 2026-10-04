package com.walletx.walletservice.business;

import com.walletx.walletservice.domain.DepositEntity;
import com.walletx.walletservice.domain.WalletCurrency;
import com.walletx.walletservice.domain.WalletEntity;
import com.walletx.walletservice.event.DepositCompletedEvent;
import com.walletx.walletservice.persistence.DepositRepository;
import com.walletx.walletservice.persistence.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final DepositRepository depositRepository;

    @Override
    public List<WalletEntity> findAllByUserId(UUID userId) {
        return walletRepository.findAllByUserId(Objects.requireNonNull(userId, "userId must not be null"));
    }

    @Override
    public WalletEntity createWallet(UUID userId) {
        UUID resolvedUserId = Objects.requireNonNull(userId, "userId must not be null");
        return walletRepository.save(new WalletEntity(resolvedUserId));
    }

    @Override
    @Transactional
    public DepositResult deposit(UUID currentUserId, UUID walletId, BigDecimal amount, WalletCurrency currency) {
        UUID resolvedUserId = Objects.requireNonNull(currentUserId, "currentUserId must not be null");
        UUID resolvedWalletId = Objects.requireNonNull(walletId, "walletId must not be null");
        WalletEntity wallet = walletRepository.findByIdAndUserIdForUpdate(resolvedWalletId, resolvedUserId)
                .orElseThrow(() -> new AccessDeniedException(
                        "Wallet not found or not owned by the authenticated user"));
        validateDeposit(wallet, amount, currency);
        BigDecimal normalizedAmount = amount.setScale(2);
        BigDecimal updatedBalance = wallet.getBalance().add(normalizedAmount).setScale(2);
        if (updatedBalance.precision() > 19) {
            throw new InvalidDepositException("deposit would exceed the maximum wallet balance");
        }
        wallet.setBalance(updatedBalance);
        Instant completedAt = Instant.now();
        DepositEntity deposit = depositRepository.save(new DepositEntity(
                wallet.getId(), resolvedUserId, normalizedAmount, currency, completedAt));
        WalletEntity updatedWallet = walletRepository.save(wallet);
        DepositCompletedEvent event = new DepositCompletedEvent(
                deposit.getId(),
                wallet.getId(),
                resolvedUserId,
                normalizedAmount,
                currency,
                completedAt
        );
        return new DepositResult(updatedWallet, event);
    }

    private void validateDeposit(WalletEntity wallet, BigDecimal amount, WalletCurrency currency) {
        if (amount == null) {
            throw new InvalidDepositException("amount must not be null");
        }
        if (currency == null) {
            throw new InvalidDepositException("currency must not be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidDepositException("amount must be greater than zero");
        }
        if (amount.scale() > 2) {
            throw new InvalidDepositException("amount must not exceed two decimal places");
        }
        if (amount.precision() - amount.scale() > 17) {
            throw new InvalidDepositException("amount exceeds the maximum supported value");
        }
        if (wallet.getCurrency() != currency) {
            throw new InvalidDepositException("wallet currency does not match deposit currency");
        }
    }
}
