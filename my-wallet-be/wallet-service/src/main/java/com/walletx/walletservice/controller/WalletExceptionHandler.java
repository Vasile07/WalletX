package com.walletx.walletservice.controller;

import com.walletx.walletservice.business.InvalidDepositException;
import com.walletx.walletservice.business.InsufficientWalletFundsException;
import com.walletx.walletservice.business.InvalidWalletTransferException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class WalletExceptionHandler {

    @ExceptionHandler(InvalidDepositException.class)
    public ResponseEntity<Void> handleInvalidDeposit(InvalidDepositException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @ExceptionHandler(InvalidWalletTransferException.class)
    public ResponseEntity<Void> handleInvalidWalletTransfer(InvalidWalletTransferException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @ExceptionHandler(InsufficientWalletFundsException.class)
    public ResponseEntity<Void> handleInsufficientWalletFunds(InsufficientWalletFundsException exception) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
    }
}
