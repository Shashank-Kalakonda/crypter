package com.minet.walletservice.repository;
import com.minet.walletservice.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Integer> {
    Wallet findByUserIdAndCoinId(int userId, int coinId);
    List<Wallet> findByUserId(int userId);
}