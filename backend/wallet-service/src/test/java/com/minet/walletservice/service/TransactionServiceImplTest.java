package com.minet.walletservice.service;

import com.minet.walletservice.dto.TransactionDTO;
import com.minet.walletservice.entity.*;
import com.minet.walletservice.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private InvestmentRepository investmentRepository;

    @Mock
    private  InvestmentService investmentService;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTransactionBuy_Success() {
        Transaction transaction1 = new Transaction(1, 100.00,
                80000, Status.pending,new Date(), 1, 4,1, Type.buy,10,20 );
        TransactionDTO responseDto =   new TransactionDTO(1, 2, "buy", 1,
                100.0, 80000, transaction1.getTimestamp().toString(),
                "pending", "john", "mary");
        int user_id = 1;
        int coin_id = 1;
        Wallet towallet = new Wallet(10,1,1,990000);
        Wallet fromwallet = new Wallet(20,11,2,30000);
        when(walletRepository.findByUserIdAndCoinId(user_id, coin_id)).thenReturn(towallet);
        when(walletRepository.findByUserIdAndCoinId(user_id,11)).thenReturn(fromwallet);
        when(transactionRepository.save(transaction1)).thenReturn(transaction1);
        Investment investment = new Investment();
        investment.setCoinId(1);
        investment.setUserId(1);
        investment.setDate(new Date());
        investment.setAmount(10000);
        List<Investment> investments = new ArrayList<>();
        investments.add(investment);
        when(investmentRepository.findByUserIdAndCoinIdAndDate(eq(1),eq(1),any())).thenReturn(new Investment());
        TransactionDTO result = transactionService.createTransaction(user_id, "buy", 1, 100.00, 8000);
        assertNotNull(result);
        assertEquals(result.getCoinValue(), responseDto.getCoinValue());
    }
    @Test
    void testCreateTransactionSell_Success() {
        Transaction transaction1 = new Transaction(1, 100.00,
                80000, Status.pending,new Date(), 1, 4,1, Type.sell,10,20 );
        TransactionDTO responseDto =   new TransactionDTO(1, 2, "sell", 1,
                100.0, 80000, transaction1.getTimestamp().toString(),
                "pending", "john", "mary");
        int user_id = 1;
        int coin_id = 1;
        Coin coin = new Coin(coin_id,"xyz123","BTC","btc.png", "Bitcoin");
        Investment investment = new Investment();
        investment.setCoinId(1);
        investment.setUserId(1);
        investment.setDate(new Date());
        investment.setAmount(10000);
        when(investmentRepository.findByUserIdAndCoinIdAndDate(eq(1),eq(1),any())).thenReturn(new Investment());
        Wallet towallet = new Wallet(10,11,1,990000);
        Wallet fromwallet = new Wallet(20,1,2,3000);
        when(walletRepository.findByUserIdAndCoinId(user_id, 11)).thenReturn(fromwallet);
        when(walletRepository.findByUserIdAndCoinId(user_id, coin_id)).thenReturn(towallet);
        when(transactionRepository.save(transaction1)).thenReturn(transaction1);
        TransactionDTO result = transactionService.createTransaction(user_id, "sell", 1, 100.00, 80000);
        assertNotNull(result);
    }
    @Test
    void testCreateTransaction_Failure() {
        Transaction transaction1 = new Transaction(1, 100.00,
                80000, Status.pending,new Date(), 1, 4,1, Type.buy,10,20 );
        when(transactionRepository.save(transaction1)).thenReturn(null);
        try {
            transactionService.createTransaction(100, "buy", 2, 100.00, 80000);
            fail("Expected RuntimeException was not thrown.");
        } catch (RuntimeException ignored) {
        }
    }
    @Test
    void testGetTransactionByUserIdAndCoinId_Success() {
        int userId = 1;
        int coinId = 123;
        Transaction transaction1 = createMockTransaction(userId, Type.buy, coinId, 10.0, 100.0);
        Transaction transaction2 = createMockTransaction(userId, Type.sell, coinId, 5.0, 50.0);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction1);
        transactions.add(transaction2);
        when(transactionRepository.findByFromUserAndCoin(userId, coinId)).thenReturn(transactions);
        List<TransactionDTO> result = transactionService.getTransactionByUserIdAndCoinId(userId, coinId);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(coinId, result.get(0).getCoinId());
        assertEquals(Type.buy.toString(), result.get(0).getAction());
        assertEquals(10.0, result.get(0).getCoinValue());
        assertEquals(100.0, result.get(0).getAmount());
    }

    @Test
    void testGetTransactionByUserIdAndCoinId_NoTransactionsFound() {
        int userId = 1;
        int coinId = 2;
        when(transactionRepository.findByFromUserAndCoin(userId, coinId)).thenReturn(null);
        try {
            transactionService.getTransactionByUserIdAndCoinId(userId, coinId);
            fail("Expected RuntimeException was not thrown.");
        } catch (RuntimeException ignored) {
        }
    }
    @Test
    void testGetTransactionByUserId_Success() {
        int userId = 1;
        Transaction transaction1 = createMockTransaction(userId, Type.buy, 123, 10.0, 100.0);
        Transaction transaction2 = createMockTransaction(userId, Type.sell, 456, 5.0, 50.0);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction1);
        transactions.add(transaction2);
        when(transactionRepository.findByFromUser(userId)).thenReturn(transactions);
        List<TransactionDTO> result = transactionService.getTransactionByUserId(userId);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testGetTransactionByUserId_NoTransactionsFound() {
        int userId = 1;
        when(transactionRepository.findByFromUser(userId)).thenReturn(null);
        try {
            transactionService.getTransactionByUserId(userId);
            fail("Expected RuntimeException was not thrown.");
        } catch (RuntimeException ignored) {
        }
    }

    private Transaction createMockTransaction(int userId, Type type, int coinId, double coinAmount,
                                              double transactionValue) {
        Transaction transaction = new Transaction();
        transaction.setFromUser(userId);
        transaction.setType(type);
        transaction.setCoin(coinId);
        transaction.setCoinAmount(coinAmount);
        transaction.setTransactionValue(transactionValue);
        transaction.setTimestamp(new Date());
        transaction.setStatus(Status.pending);
        return transaction;
    }
}

