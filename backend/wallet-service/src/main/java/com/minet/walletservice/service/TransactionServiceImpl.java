package com.minet.walletservice.service;
import com.minet.walletservice.dto.TransactionDTO;
import com.minet.walletservice.entity.*;
import com.minet.walletservice.exceptions.IDNotFoundException;
import com.minet.walletservice.exceptions.NegativeBalanceException;
import com.minet.walletservice.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;
import com.minet.walletservice.service.InvestmentService;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService{
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private InvestmentService investmentService;


    public TransactionDTO createTransaction(int userId, String transactionType, int coinId, double coinAmount,
                                            double transactionValue) {

        Transaction transaction = new Transaction();
        transaction.setCoinAmount(coinAmount);
        transaction.setTransactionValue(transactionValue);
        transaction.setStatus(Status.success);
        transaction.setTimestamp(new Date());
        transaction.setCoin(coinId);

        if (Objects.equals(transactionType, "buy")) {
            Wallet towallet = walletRepository.findByUserIdAndCoinId(userId, coinId);

            Wallet fromwallet = walletRepository.findByUserIdAndCoinId(userId, 11);
            transaction.setFromUser(41);
            transaction.setType(Type.buy);
            if(towallet!=null && fromwallet!=null){
                transaction.setToUser(userId);
                transaction.setToWallet(towallet.getId());
                transaction.setFromWallet(fromwallet.getId());
                towallet.setBalance(Math.abs(towallet.getBalance() + coinAmount));
                double  newFromWalletBalance= fromwallet.getBalance() - transactionValue;
                if(newFromWalletBalance < 0){
                    throw  new NegativeBalanceException("Wallet balance is negative, transaction cannot be proceeded");
                }
                fromwallet.setBalance(newFromWalletBalance);
                updateInvestment(coinId,userId,transaction);
            }
            else if(towallet == null){
                Wallet wallet = new Wallet();
                wallet.setCoinId(coinId);
                wallet.setUserId(userId);
                wallet.setBalance(transaction.getCoinAmount());
                walletRepository.save(wallet);
                transaction.setToUser(userId);
                transaction.setToWallet(wallet.getId());
                transaction.setFromWallet(fromwallet.getId());
                double  newFromWalletBalance= fromwallet.getBalance() - transactionValue;
                fromwallet.setBalance(newFromWalletBalance);
                updateInvestment(coinId,userId,transaction);
            }
            else throw new IDNotFoundException("UserID is invalid or Wallet does not exist for userID and coinID");

        }
        if (Objects.equals(transactionType, "sell")) {
            Wallet towallet = walletRepository.findByUserIdAndCoinId(userId, 11);
            Wallet fromwallet = walletRepository.findByUserIdAndCoinId(userId, coinId);
            transaction.setToUser(41);
            transaction.setType(Type.sell);
            if(towallet!=null && fromwallet!=null){
                transaction.setFromUser(userId);
                transaction.setFromWallet(fromwallet.getId());
                transaction.setToWallet(towallet.getId());
                double  newFromWalletBalance= fromwallet.getBalance() - coinAmount;
                if(newFromWalletBalance < 0){
                    throw  new NegativeBalanceException("Wallet balance is negative, transaction cannot be proceeded");
                }
                towallet.setBalance(towallet.getBalance() + transactionValue);
                fromwallet.setBalance(newFromWalletBalance);
                updateInvestment(coinId,userId,transaction);
            } else throw new IDNotFoundException("UserID is invalid or Wallet does not exist for userID and coinID");

        }
        log.debug("Transaction",transaction);
        transactionRepository.save(transaction);
        return new TransactionDTO(transaction.getId(), userId, transactionType, coinId,
                coinAmount, transactionValue, transaction.getTimestamp().toString(),
                transaction.getStatus().toString(), "john", "mary");

    }

    public TransactionDTO convertentitytoDTO(Transaction transaction){
        if(Objects.equals(transaction.getType().toString(), "buy")){
            return new TransactionDTO(transaction.getId(), transaction.getFromUser(),
                    transaction.getType().toString(), transaction.getCoin(), transaction.getCoinAmount(),
                    transaction.getTransactionValue(), transaction.getTimestamp().toString(),
                    transaction.getStatus().toString(), "john", "mary");
        }
        return new TransactionDTO(transaction.getId(), transaction.getToUser(),
                transaction.getType().toString(), transaction.getCoin(), transaction.getCoinAmount(),
                transaction.getTransactionValue(), transaction.getTimestamp().toString(),
                transaction.getStatus().toString(), "john", "mary");
    }

    @Override
    public List<TransactionDTO> getTransactionByUserIdAndCoinId(int userId, int coinId) {
        List<Transaction> transactions = new ArrayList<>();
        List<Transaction> fromTransactions = transactionRepository.findByFromUserAndCoin(userId, coinId);
        List<Transaction> toTransactions = transactionRepository.findByToUserAndCoin(userId, coinId);
        transactions.addAll(fromTransactions);
        transactions.addAll(toTransactions);
        if (!transactions.isEmpty()) {
            return transactions.stream()
                    .map(this::convertentitytoDTO)
                    .collect(Collectors.toList());
        } else {
            throw new IDNotFoundException("Bad coinID or userID");
        }
    }
@Override
    public List<TransactionDTO> getTransactionByUserId(int userId) {
        List<Transaction> transactions = new ArrayList<>();
        List<Transaction> fromTransactions = transactionRepository.findByFromUser(userId);
        List<Transaction> toTransactions = transactionRepository.findByToUser(userId);
        transactions.addAll(fromTransactions);
        transactions.addAll(toTransactions);
        if (!transactions.isEmpty()) {
            return transactions.stream()
                    .map(this::convertentitytoDTO)
                    .collect(Collectors.toList());
        } else {
            throw new IDNotFoundException("No user found with specified userID");
        }
    }
    private  void updateInvestment(int coinId,int userId,Transaction transaction){
        Date currentDate= new Date();
        Investment investment = investmentService.findByUserIdAndCoinIdAndDate(userId,coinId,currentDate);
        if(investment != null){
            if(transaction.getType() == Type.buy){
                investment.setAmount(investment.getAmount() + transaction.getTransactionValue());
            }
            else {
                investment.setAmount(investment.getAmount() - transaction.getTransactionValue());
            }
            investmentService.addInvestment(investment);
        }
        else {
            Investment newInvestment = new Investment();
            if(transaction.getType() == Type.buy) {
                newInvestment.setAmount(transaction.getTransactionValue());
            }
            else{
                newInvestment.setAmount(0-transaction.getTransactionValue());
            }
            newInvestment.setDate(currentDate);
            newInvestment.setUserId(userId);
            newInvestment.setCoinId(coinId);
            investmentService.addInvestment(newInvestment);
        }
    }
}
