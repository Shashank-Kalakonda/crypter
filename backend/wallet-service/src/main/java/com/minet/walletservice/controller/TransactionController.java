package com.minet.walletservice.controller;
import com.minet.walletservice.dto.TransactionDTO;
import com.minet.walletservice.dto.TransactionRequest;
import com.minet.walletservice.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import lombok.Data;
import java.util.List;

@RestController
@Data
@RequestMapping("/api/wallets/transactions")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @GetMapping
    public ResponseEntity<?> getTransactionByUserIdAndCoinId(
            @RequestParam(value = "userId", required = true) Integer userID,
            @RequestParam(value = "coinId", required = false) Integer coinID
    ) {
        try {
            if (userID != null && coinID == null) {
                List<TransactionDTO> transaction = transactionService.getTransactionByUserId(userID);
                return ResponseEntity.ok(transaction);
            }
        }catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        try {
            if (coinID != null && userID != null) {
                List<TransactionDTO> transaction = transactionService.getTransactionByUserIdAndCoinId(userID, coinID);
                return ResponseEntity.ok(transaction);
            }
        }catch  (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
       return null;
    }
    @PostMapping
    public ResponseEntity<?> createTransaction(@RequestBody TransactionRequest request) {
        try {
            int userId = request.getUserId();
            String type = request.getTransactionType();
            int coinId = request.getCoinId();
            double amount = request.getCoinAmount();
            double value = request.getTransactionValue();
            if (userId <= 0 || type == null || type.isEmpty() || coinId <= 0 || amount <= 0 || value <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request parameters");
            }
            TransactionDTO transaction = transactionService.createTransaction(userId, type, coinId, amount, value);
            return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

}