package com.walletx.transferservice.business;

public class InvalidTransferException extends IllegalArgumentException {

    public InvalidTransferException(String message) {
        super(message);
    }
}
