package com.minet.cryptoservice.service;

import com.minet.cryptoservice.dto.PriceResponseDTO;
import com.minet.cryptoservice.exception.CoinNotFoundException;
import org.springframework.http.ResponseEntity;

public interface PriceService {
    ResponseEntity<PriceResponseDTO> getPriceHistory(Integer coinId) throws CoinNotFoundException;
}
