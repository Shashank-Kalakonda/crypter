package com.minet.walletservice.dto;

import lombok.*;

@Data
@Getter
@Setter
public class TransactionRequest {
    Integer userId;
    String transactionType;
    Integer coinId;
    Double coinAmount;
    Double transactionValue;

}

