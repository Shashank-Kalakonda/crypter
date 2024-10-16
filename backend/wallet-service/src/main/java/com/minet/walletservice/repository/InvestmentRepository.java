package com.minet.walletservice.repository;
import com.minet.walletservice.entity.Investment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface InvestmentRepository extends JpaRepository<Investment, Integer> {
    List<Investment> findByUserId(int userId);
    @Query("SELECT i FROM Investment i WHERE i.userId = :userId AND i.coinId = :coinId AND DATE(i.date) = DATE(:date)")
    Investment findByUserIdAndCoinIdAndDate(int userId, int coinId, Date date);


}