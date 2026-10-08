package com.walletx.walletservice.business;

public class InvalidWalletTransferException extends IllegalArgumentException {

    public InvalidWalletTransferException(String message) {
        super(message);
    }
}
