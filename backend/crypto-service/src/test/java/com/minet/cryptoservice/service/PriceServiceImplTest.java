package com.minet.cryptoservice.service;

import com.minet.cryptoservice.dto.GeckoPriceResponseDTO;
import com.minet.cryptoservice.dto.PriceResponseDTO;
import com.minet.cryptoservice.exception.CoinNotFoundException;
import com.minet.cryptoservice.model.Coin;
import com.minet.cryptoservice.repository.CoinRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class PriceServiceImplTest {

    @InjectMocks
    private PriceServiceImpl priceService;

    @Mock
    private CoinRepository coinRepository;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetPriceHistory_ValidCoinId() throws CoinNotFoundException {

        int coinId = 1;
        String apiId = "bitcoin";
        Coin mockCoin = new Coin();
        mockCoin.setId(coinId);
        mockCoin.setApiId(apiId);

        GeckoPriceResponseDTO mockGeckoResponse = new GeckoPriceResponseDTO();
        mockGeckoResponse.setPrices(Arrays.asList(
                Arrays.asList(1627363200000L, 35000.0),
                Arrays.asList(1627449600000L, 36000.0)
        ));

        when(coinRepository.findById(coinId)).thenReturn(java.util.Optional.of(mockCoin));

        when(restTemplate.getForObject(anyString(), eq(GeckoPriceResponseDTO.class))).thenReturn(mockGeckoResponse);
        ResponseEntity<PriceResponseDTO> result = priceService.getPriceHistory(coinId);

        assertEquals(HttpStatus.OK, result.getStatusCode());

        PriceResponseDTO responseDTO = result.getBody();
        assertEquals(2, responseDTO.getPriceHistory().size());
        assertEquals(35000.0, responseDTO.getPriceHistory().get(0).getPrice());
        assertEquals("2021-07-27", responseDTO.getPriceHistory().get(0).getDate());
        assertEquals(36000.0, responseDTO.getPriceHistory().get(1).getPrice());
        assertEquals("2021-07-28", responseDTO.getPriceHistory().get(1).getDate());

        verify(coinRepository, times(1)).findById(coinId);
    }

    @Test
    void testGetPriceHistory_InvalidCoinId() throws CoinNotFoundException {

        int coinId = 999;

        when(coinRepository.findById(coinId)).thenReturn(java.util.Optional.empty());

        Assertions.assertThrows(CoinNotFoundException.class,()->{
            priceService.getPriceHistory(coinId);
        });


        verify(coinRepository, times(1)).findById(coinId);
        verifyNoInteractions(restTemplate);
    }
}