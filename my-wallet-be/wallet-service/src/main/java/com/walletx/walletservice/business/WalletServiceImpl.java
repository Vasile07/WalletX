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

    @Override
    @Transactional
    public void transfer(
            UUID currentUserId,
            UUID senderWalletId,
            UUID receiverWalletId,
            BigDecimal amount,
            WalletCurrency currency
    ) {
        UUID resolvedUserId = Objects.requireNonNull(currentUserId, "currentUserId must not be null");
        UUID resolvedSenderWalletId = Objects.requireNonNull(senderWalletId, "senderWalletId must not be null");
        UUID resolvedReceiverWalletId = Objects.requireNonNull(receiverWalletId, "receiverWalletId must not be null");
        validateTransferRequest(resolvedSenderWalletId, resolvedReceiverWalletId, amount, currency);

        List<WalletEntity> wallets = walletRepository.findAllByIdInForUpdate(
                List.of(resolvedSenderWalletId, resolvedReceiverWalletId));
        WalletEntity senderWallet = wallets.stream()
                .filter(wallet -> resolvedSenderWalletId.equals(wallet.getId()))
                .findFirst()
                .orElseThrow(() -> new AccessDeniedException(
                        "Sender wallet not found or not owned by the authenticated user"));
        WalletEntity receiverWallet = wallets.stream()
                .filter(wallet -> resolvedReceiverWalletId.equals(wallet.getId()))
                .findFirst()
                .orElseThrow(() -> new InvalidWalletTransferException("receiver wallet not found"));

        if (!resolvedUserId.equals(senderWallet.getUserId())) {
            throw new AccessDeniedException("Sender wallet is not owned by the authenticated user");
        }
        if (senderWallet.getCurrency() != currency || receiverWallet.getCurrency() != currency) {
            throw new InvalidWalletTransferException("wallet currency does not match transfer currency");
        }
        if (senderWallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientWalletFundsException("Sender wallet does not have sufficient funds");
        }

        BigDecimal normalizedAmount = amount.setScale(2);
        BigDecimal updatedSenderBalance = senderWallet.getBalance().subtract(normalizedAmount);
        BigDecimal updatedReceiverBalance = receiverWallet.getBalance().add(normalizedAmount);
        if (updatedSenderBalance.precision() > 19 || updatedReceiverBalance.precision() > 19) {
            throw new InvalidWalletTransferException("transfer would exceed the maximum wallet balance");
        }

        senderWallet.setBalance(updatedSenderBalance);
        receiverWallet.setBalance(updatedReceiverBalance);
        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);
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

    private void validateTransferRequest(
            UUID senderWalletId,
            UUID receiverWalletId,
            BigDecimal amount,
            WalletCurrency currency
    ) {
        if (senderWalletId.equals(receiverWalletId)) {
            throw new InvalidWalletTransferException("sender and receiver wallets must be different");
        }
        if (amount == null) {
            throw new InvalidWalletTransferException("amount must not be null");
        }
        if (currency == null) {
            throw new InvalidWalletTransferException("currency must not be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidWalletTransferException("amount must be greater than zero");
        }
        if (amount.scale() > 2) {
            throw new InvalidWalletTransferException("amount must not exceed two decimal places");
        }
        if (amount.precision() - amount.scale() > 17) {
            throw new InvalidWalletTransferException("amount exceeds the maximum supported value");
        }
    }
}
