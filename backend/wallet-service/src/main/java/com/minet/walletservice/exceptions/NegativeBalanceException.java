package com.minet.walletservice.exceptions;

public class NegativeBalanceException extends RuntimeException {
    private final String message;

    public NegativeBalanceException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

