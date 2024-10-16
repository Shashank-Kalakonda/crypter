package com.minet.cryptoservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class CoinNotFoundException extends Exception {
    public CoinNotFoundException(String message) {
        super(message);
    }
}
