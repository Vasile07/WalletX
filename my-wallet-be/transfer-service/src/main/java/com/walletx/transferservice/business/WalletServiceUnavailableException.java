package com.walletx.transferservice.business;

public class WalletServiceUnavailableException extends RuntimeException {

    public WalletServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
