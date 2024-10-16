package com.minet.walletservice.service;
import com.minet.walletservice.dto.*;
import java.util.List;

public interface TransactionService {

    TransactionDTO createTransaction(int userId, String transactionType, int coinId, double coinAmount,
                                     double transactionValue);
    List<TransactionDTO> getTransactionByUserIdAndCoinId(int userId, int coinId);
    List<TransactionDTO> getTransactionByUserId(int userId);
}