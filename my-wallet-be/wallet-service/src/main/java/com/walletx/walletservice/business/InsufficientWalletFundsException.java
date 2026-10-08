package com.walletx.walletservice.business;

public class InsufficientWalletFundsException extends IllegalStateException {

    public InsufficientWalletFundsException(String message) {
        super(message);
    }
}
