package com.walletx.transferservice.business;

import com.walletx.transferservice.controller.TransferRequest;
import com.walletx.transferservice.domain.Transfer;
import com.walletx.transferservice.persistence.TransferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
class TransferServiceImplTests {

    private static final String AUTHORIZATION_HEADER = "Bearer " + "test-token";

    @Autowired
    private TransferServiceImpl transferService;

    @SpyBean
    private TransferRepository transferRepository;

    @MockBean
    private WalletTransferClient walletTransferClient;

    @BeforeEach
    void setUp() {
        transferRepository.deleteAll();
    }

    @Test
    void createsValidTransferAfterWalletServiceCompletesIt() {
        UUID senderWalletId = UUID.randomUUID();
        UUID receiverWalletId = UUID.randomUUID();

        Transfer transfer = transferService.createTransfer(
                request(senderWalletId, receiverWalletId, "15.50", "RON"),
                AUTHORIZATION_HEADER);

        assertEquals(senderWalletId, transfer.getSenderWalletId());
        assertEquals(receiverWalletId, transfer.getReceiverWalletId());
        assertEquals(new BigDecimal("15.50"), transfer.getAmount());
        assertEquals("RON", transfer.getCurrency());
        assertNotNull(transfer.getCompletedAt());
        assertEquals(1, transferRepository.count());
        verify(walletTransferClient).executeTransfer(
                senderWalletId, receiverWalletId, new BigDecimal("15.50"), "RON", AUTHORIZATION_HEADER);
    }

    @Test
    void rejectsWalletServiceFailureWithoutPersistingTransfer() {
        UUID senderWalletId = UUID.randomUUID();
        UUID receiverWalletId = UUID.randomUUID();
        doThrow(new InsufficientFundsException("insufficient funds"))
                .when(walletTransferClient).executeTransfer(
                        senderWalletId, receiverWalletId, new BigDecimal("10.00"), "RON", AUTHORIZATION_HEADER);

        assertThrows(InsufficientFundsException.class, () -> transferService.createTransfer(
                request(senderWalletId, receiverWalletId, "10.00", "RON"), AUTHORIZATION_HEADER));
        assertEquals(0, transferRepository.count());
    }

    @Test
    void rejectsInvalidAmountWithoutCallingWalletService() {
        assertThrows(InvalidTransferException.class, () -> transferService.createTransfer(
                request(UUID.randomUUID(), UUID.randomUUID(), "0.00", "RON"), AUTHORIZATION_HEADER));
        assertEquals(0, transferRepository.count());
    }

    @Test
    void transferHistoryPersistenceFailureOccursAfterWalletServiceCall() {
        UUID senderWalletId = UUID.randomUUID();
        UUID receiverWalletId = UUID.randomUUID();
        doThrow(new DataIntegrityViolationException("simulated persistence failure"))
                .when(transferRepository).save(any(Transfer.class));

        assertThrows(DataIntegrityViolationException.class, () -> transferService.createTransfer(
                request(senderWalletId, receiverWalletId, "5.00", "RON"), AUTHORIZATION_HEADER));

        verify(walletTransferClient).executeTransfer(
                senderWalletId, receiverWalletId, new BigDecimal("5.00"), "RON", AUTHORIZATION_HEADER);
        assertEquals(0, transferRepository.count());
    }

    private TransferRequest request(UUID senderId, UUID receiverId, String amount, String currency) {
        TransferRequest request = new TransferRequest();
        request.setSenderWalletId(senderId);
        request.setReceiverWalletId(receiverId);
        request.setAmount(new BigDecimal(amount));
        request.setCurrency(currency);
        return request;
    }
}
