package com.minet.cryptoservice.service;

import com.minet.cryptoservice.dto.CoinResponseDTO;
import com.minet.cryptoservice.model.Coin;
import com.minet.cryptoservice.model.GeckoData;
import com.minet.cryptoservice.repository.CoinRepository;
import com.minet.cryptoservice.repository.GeckoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
 class CoinServiceImplTest {
    @InjectMocks
    private CoinServiceImpl coinService;

    @Mock
    private CoinRepository coinRepository;

    @Mock
    private GeckoRepository geckoRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
     void testGetAllCoins() {

        List<Coin> mockCoinList = new ArrayList<>();
        mockCoinList.add(new Coin(1,"bitcoin","BTC","http://example.com","Bitcoin"));

        List<GeckoData> geckoData = new ArrayList<>();
        geckoData.add(new GeckoData("bitcoin",12781.128, BigDecimal.valueOf(120921), 1.22F,12921.21,19281.2));
        when(geckoRepository.findAll()).thenReturn(geckoData);

        when(coinRepository.findAll()).thenReturn(mockCoinList);

        List<CoinResponseDTO> result = coinService.getAllCoins();

        assertEquals(mockCoinList.size(), result.size());

    }

    @Test
     void testGetCoinByIdValid() {
        int coinId = 1;
        Coin mockCoin = new Coin();
        mockCoin.setApiId("bitcoin");

        GeckoData mockGecko= new GeckoData();

        when(coinRepository.findById(coinId)).thenReturn(Optional.of(mockCoin));
        when(geckoRepository.findById("bitcoin")).thenReturn(Optional.of(mockGecko));

        ResponseEntity<CoinResponseDTO> result = coinService.getCoin(coinId);

        assertEquals(HttpStatus.OK, result.getStatusCode());

    }

    @Test
        void testGetCoinByIdInvalid() {
        int coinId = 999;
        when(coinRepository.findById(coinId)).thenReturn(Optional.empty());

        ResponseEntity<CoinResponseDTO> result = coinService.getCoin(coinId);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());

    }
}
