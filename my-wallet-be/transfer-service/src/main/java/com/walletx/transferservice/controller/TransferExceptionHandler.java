package com.walletx.transferservice.controller;

import com.walletx.transferservice.business.InsufficientFundsException;
import com.walletx.transferservice.business.InvalidTransferException;
import com.walletx.transferservice.business.WalletServiceUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TransferExceptionHandler {

    @ExceptionHandler(InvalidTransferException.class)
    public ResponseEntity<Void> handleInvalidTransfer(InvalidTransferException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<Void> handleInsufficientFunds(InsufficientFundsException exception) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Void> handleAccessDenied(AccessDeniedException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @ExceptionHandler(WalletServiceUnavailableException.class)
    public ResponseEntity<Void> handleWalletServiceUnavailable(WalletServiceUnavailableException exception) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
    }
}
