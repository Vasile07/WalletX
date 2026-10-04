package com.walletx.walletservice.business;

import com.walletx.walletservice.domain.DepositEntity;
import com.walletx.walletservice.domain.WalletCurrency;
import com.walletx.walletservice.domain.WalletEntity;
import com.walletx.walletservice.persistence.DepositRepository;
import com.walletx.walletservice.persistence.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletServiceImplTests {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private DepositRepository depositRepository;

    @InjectMocks
    private WalletServiceImpl walletService;

    @Test
    void updatesOwnedWalletAndPersistsCompletedDeposit() {
        UUID userId = UUID.randomUUID();
        WalletEntity wallet = new WalletEntity(userId);
        when(walletRepository.findByIdAndUserIdForUpdate(wallet.getId(), userId))
                .thenReturn(Optional.of(wallet));
        when(depositRepository.save(any(DepositEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(walletRepository.save(wallet)).thenReturn(wallet);

        DepositResult result = walletService.deposit(
                userId, wallet.getId(), new BigDecimal("10"), WalletCurrency.RON);

        assertEquals(new BigDecimal("10.00"), result.getWallet().getBalance());
        assertEquals(wallet.getId(), result.getEvent().getWalletId());
        assertEquals(userId, result.getEvent().getUserId());
        assertEquals(new BigDecimal("10.00"), result.getEvent().getAmount());
        assertEquals(WalletCurrency.RON, result.getEvent().getCurrency());
        assertNotNull(result.getEvent().getDepositId());

        ArgumentCaptor<DepositEntity> depositCaptor = ArgumentCaptor.forClass(DepositEntity.class);
        verify(depositRepository).save(depositCaptor.capture());
        assertEquals(wallet.getId(), depositCaptor.getValue().getWalletId());
        assertEquals(userId, depositCaptor.getValue().getUserId());
        assertEquals(new BigDecimal("10.00"), depositCaptor.getValue().getAmount());
        assertEquals(result.getEvent().getDepositId(), depositCaptor.getValue().getId());
        verify(walletRepository).save(wallet);
    }

    @Test
    void rejectsWalletNotOwnedByCurrentUserWithoutPersistingAnything() {
        UUID userId = UUID.randomUUID();
        UUID walletId = UUID.randomUUID();
        when(walletRepository.findByIdAndUserIdForUpdate(walletId, userId)).thenReturn(Optional.empty());

        assertThrows(AccessDeniedException.class,
                () -> walletService.deposit(userId, walletId, new BigDecimal("10"), WalletCurrency.RON));

        verify(walletRepository, never()).save(any(WalletEntity.class));
        verify(depositRepository, never()).save(any(DepositEntity.class));
    }

    @Test
    void rejectsInvalidAmountWithoutPersistingDeposit() {
        UUID userId = UUID.randomUUID();
        WalletEntity wallet = new WalletEntity(userId);
        when(walletRepository.findByIdAndUserIdForUpdate(wallet.getId(), userId))
                .thenReturn(Optional.of(wallet));

        assertThrows(InvalidDepositException.class,
                () -> walletService.deposit(userId, wallet.getId(), BigDecimal.ZERO, WalletCurrency.RON));

        verify(depositRepository, never()).save(any(DepositEntity.class));
        verify(walletRepository, never()).save(any(WalletEntity.class));
    }

    @Test
    void rejectsUnsupportedCurrencyWithoutPersistingDeposit() {
        UUID userId = UUID.randomUUID();
        WalletEntity wallet = new WalletEntity(userId);
        when(walletRepository.findByIdAndUserIdForUpdate(wallet.getId(), userId))
                .thenReturn(Optional.of(wallet));

        assertThrows(InvalidDepositException.class,
                () -> walletService.deposit(userId, wallet.getId(), new BigDecimal("1.00"), null));

        verify(depositRepository, never()).save(any(DepositEntity.class));
        verify(walletRepository, never()).save(any(WalletEntity.class));
    }

    @Test
    void rejectsExcessPrecisionWithoutPersistingDeposit() {
        UUID userId = UUID.randomUUID();
        WalletEntity wallet = new WalletEntity(userId);
        when(walletRepository.findByIdAndUserIdForUpdate(wallet.getId(), userId))
                .thenReturn(Optional.of(wallet));

        assertThrows(InvalidDepositException.class,
                () -> walletService.deposit(userId, wallet.getId(), new BigDecimal("1.001"), WalletCurrency.RON));

        verify(depositRepository, never()).save(any(DepositEntity.class));
        verify(walletRepository, never()).save(any(WalletEntity.class));
    }

    @Test
    void rejectsDepositThatWouldOverflowWalletBalance() {
        UUID userId = UUID.randomUUID();
        WalletEntity wallet = new WalletEntity(userId);
        wallet.setBalance(new BigDecimal("99999999999999999.99"));
        when(walletRepository.findByIdAndUserIdForUpdate(wallet.getId(), userId))
                .thenReturn(Optional.of(wallet));

        assertThrows(InvalidDepositException.class,
                () -> walletService.deposit(userId, wallet.getId(), BigDecimal.ONE, WalletCurrency.RON));

        verify(depositRepository, never()).save(any(DepositEntity.class));
        verify(walletRepository, never()).save(any(WalletEntity.class));
    }
}
