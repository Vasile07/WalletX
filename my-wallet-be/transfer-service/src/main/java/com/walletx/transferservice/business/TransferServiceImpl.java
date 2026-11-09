package com.walletx.transferservice.business;

import com.walletx.transferservice.controller.TransferRequest;
import com.walletx.transferservice.domain.Transfer;
import com.walletx.transferservice.messaging.TransferMessagePublisher;
import com.walletx.transferservice.persistence.TransferRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class TransferServiceImpl implements TransferService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransferServiceImpl.class);

    private final TransferRepository transferRepository;
    private final WalletTransferClient walletTransferClient;
    private final TransferMessagePublisher transferMessagePublisher;

    public TransferServiceImpl(
            TransferRepository transferRepository,
            WalletTransferClient walletTransferClient,
            TransferMessagePublisher transferMessagePublisher
    ) {
        this.transferRepository = transferRepository;
        this.walletTransferClient = walletTransferClient;
        this.transferMessagePublisher = transferMessagePublisher;
    }

    @Override
    @Transactional
    public Transfer createTransfer(TransferRequest request, String authorizationHeader) {
        if (request == null) {
            throw new InvalidTransferException("request must not be null");
        }
        if (request.getSenderWalletId() == null || request.getReceiverWalletId() == null) {
            throw new InvalidTransferException("senderWalletId and receiverWalletId must not be null");
        }
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new AccessDeniedException("Authentication required");
        }

        var senderWalletId = request.getSenderWalletId();
        var receiverWalletId = request.getReceiverWalletId();
        BigDecimal rawAmount = request.getAmount();
        String currency = normalizeCurrency(request.getCurrency());

        validateTransfer(senderWalletId, receiverWalletId, rawAmount, currency);
        BigDecimal amount = normalizeAmount(rawAmount);
        String idempotencyKey = normalizeIdempotencyKey(request.getIdempotencyKey());

        if (idempotencyKey != null) {
            return transferRepository.findByIdempotencyKey(idempotencyKey)
                    .orElseGet(() -> invokeWalletTransferAndPersist(senderWalletId, receiverWalletId, amount, currency, idempotencyKey, authorizationHeader));
        }

        return invokeWalletTransferAndPersist(senderWalletId, receiverWalletId, amount, currency, null, authorizationHeader);
    }

    @Override
    public List<Transfer> findAllTransfers() {
        return transferRepository.findAll();
    }

    @Override
    public List<Transfer> findTransfersForUser(UUID currentUserId, String authorizationHeader) {
        if (currentUserId == null) {
            throw new AccessDeniedException("Authentication required");
        }
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new AccessDeniedException("Authentication required");
        }

        List<UUID> walletIds = walletTransferClient.findWalletIdsForUser(currentUserId, authorizationHeader);
        if (walletIds == null || walletIds.isEmpty()) {
            return List.of();
        }

        return transferRepository.findAllBySenderWalletIdInOrReceiverWalletIdInOrderByCompletedAtDesc(
                walletIds,
                walletIds
        );
    }

    private void validateTransfer(UUID senderWalletId, UUID receiverWalletId, BigDecimal amount, String currency) {
        if (currency == null || !"RON".equalsIgnoreCase(currency)) {
            throw new InvalidTransferException("currency must be RON");
        }
        if (senderWalletId == null || receiverWalletId == null) {
            throw new InvalidTransferException("senderWalletId and receiverWalletId must not be null");
        }
        if (amount == null) {
            throw new InvalidTransferException("amount must not be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransferException("amount must be greater than zero");
        }
        if (amount.scale() > 2) {
            throw new InvalidTransferException("amount must not exceed two decimal places");
        }
        if (amount.precision() - amount.scale() > 17) {
            throw new InvalidTransferException("amount exceeds the maximum supported value");
        }
        if (senderWalletId.equals(receiverWalletId)) {
            throw new InvalidTransferException("sender and receiver wallets must be different");
        }
    }

    private Transfer invokeWalletTransferAndPersist(
            UUID senderWalletId,
            UUID receiverWalletId,
            BigDecimal amount,
            String currency,
            String idempotencyKey,
            String authorizationHeader
    ) {
        walletTransferClient.executeTransfer(senderWalletId, receiverWalletId, amount, currency, authorizationHeader);
        Transfer transfer = new Transfer(senderWalletId, receiverWalletId, amount, currency, idempotencyKey);
        try {
            Transfer savedTransfer = transferRepository.saveAndFlush(transfer);
            try {
                transferMessagePublisher.publishTransferCompleted(savedTransfer);
            } catch (AmqpException exception) {
                LOGGER.warn("Transfer {} was recorded but could not be published to RabbitMQ", savedTransfer.getId(), exception);
            }
            return savedTransfer;
        } catch (DataIntegrityViolationException exception) {
            if (idempotencyKey == null) {
                throw exception;
            }
            return transferRepository.findByIdempotencyKey(idempotencyKey)
                    .orElseThrow(() -> exception);
        }
    }

    private String normalizeCurrency(String currency) {
        return currency == null ? null : currency.trim().toUpperCase(Locale.ROOT);
    }

    private BigDecimal normalizeAmount(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP);
    }

    private String normalizeIdempotencyKey(String idempotencyKey) {
        if (idempotencyKey == null) {
            return null;
        }
        String normalized = idempotencyKey.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
