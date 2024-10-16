package com.minet.cryptoservice.service;

import com.minet.cryptoservice.dto.GeckoResponseDTO;
import com.minet.cryptoservice.model.GeckoData;
import com.minet.cryptoservice.repository.GeckoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.Arrays;
import java.util.List;

@Service
public class CoinUpdateService {
    @Autowired
    private GeckoRepository geckoRepository;
    @Scheduled(fixedRate = 720000)
    public void updateGeckoData(){
        ModelMapper modelMapper = new ModelMapper();
        List<GeckoResponseDTO> geckoResponseDTOS = fetchGeckoData("bitcoin,ethereum,binancecoin,ethereum-classic,tether,cardano,ripple,dogecoin,usd-coin");
        for (GeckoResponseDTO geckoResponse : geckoResponseDTOS ) {
            GeckoData newGeckoData= modelMapper.map(geckoResponse, GeckoData.class);
            geckoRepository.save(newGeckoData);

        }

    }
    public List<GeckoResponseDTO> fetchGeckoData(String ids) {
        String geckoUrl = "https://api.coingecko.com/api/v3/coins/markets";
        String vsCurrency = "usd";
        String urlTemplate =
                UriComponentsBuilder.fromHttpUrl(geckoUrl)
                        .queryParam("vs_currency", vsCurrency)
                        .queryParam("ids", ids)
                        .encode()
                        .toUriString();

        RestTemplate restTemplate = new RestTemplate();
        GeckoResponseDTO[] geckoResponse =
                restTemplate.getForObject(urlTemplate, GeckoResponseDTO[].class);
        return Arrays.asList(geckoResponse);
    }

}
