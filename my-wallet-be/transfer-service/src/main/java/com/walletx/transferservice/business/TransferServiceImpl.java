package com.walletx.transferservice.business;

import com.walletx.transferservice.controller.TransferRequest;
import com.walletx.transferservice.domain.Transfer;
import com.walletx.transferservice.persistence.TransferRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class TransferServiceImpl implements TransferService {

    private final TransferRepository transferRepository;
    private final WalletTransferClient walletTransferClient;

    public TransferServiceImpl(
            TransferRepository transferRepository,
            WalletTransferClient walletTransferClient
    ) {
        this.transferRepository = transferRepository;
        this.walletTransferClient = walletTransferClient;
    }

    @Override
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
        walletTransferClient.executeTransfer(
                senderWalletId, receiverWalletId, amount, currency, authorizationHeader);
        Transfer transfer = new Transfer(senderWalletId, receiverWalletId, amount, currency);
        return transferRepository.save(transfer);
    }

    @Override
    public List<Transfer> findAllTransfers() {
        return transferRepository.findAll();
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

    private String normalizeCurrency(String currency) {
        return currency == null ? null : currency.trim().toUpperCase();
    }

    private BigDecimal normalizeAmount(BigDecimal amount) {
        return amount.setScale(2);
    }
}
