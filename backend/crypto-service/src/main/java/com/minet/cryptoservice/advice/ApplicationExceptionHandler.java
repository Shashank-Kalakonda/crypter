package com.minet.cryptoservice.advice;

import com.minet.cryptoservice.exception.CoinNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApplicationExceptionHandler {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler (CoinNotFoundException.class)
    public Map<String,String> handleCoinNotFound(CoinNotFoundException ex){
        Map errorMap= new HashMap<>();
        errorMap.put("Error Occurred",ex.getMessage());
        return errorMap;
    }
}
