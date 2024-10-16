package com.minet.cryptoservice.repository;

import com.minet.cryptoservice.model.GeckoData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeckoRepository extends JpaRepository<GeckoData,String> {}
