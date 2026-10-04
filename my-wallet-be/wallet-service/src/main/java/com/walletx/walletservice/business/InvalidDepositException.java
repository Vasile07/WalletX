package com.walletx.walletservice.business;

public class InvalidDepositException extends RuntimeException {

    public InvalidDepositException(String message) {
        super(message);
    }
}
