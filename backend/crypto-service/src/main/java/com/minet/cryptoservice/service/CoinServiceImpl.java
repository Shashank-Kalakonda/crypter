package com.minet.cryptoservice.service;

import com.minet.cryptoservice.dto.DBResponseDTO;
import com.minet.cryptoservice.dto.GeckoResponseDTO;
import com.minet.cryptoservice.dto.CoinResponseDTO;
import com.minet.cryptoservice.model.Coin;
import com.minet.cryptoservice.model.GeckoData;
import com.minet.cryptoservice.repository.CoinRepository;
import com.minet.cryptoservice.repository.GeckoRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CoinServiceImpl implements CoinService {
  @Autowired private CoinRepository coinRepository;

  @Autowired private GeckoRepository geckoRepository;

  public List<DBResponseDTO> fetchDBData() {
    List<Coin> coins = coinRepository.findAll();
    ModelMapper modelMapper = new ModelMapper();
    return coins.stream()
            .map(coin -> modelMapper.map(coin, DBResponseDTO.class))
            .collect(Collectors.toList());
  }


  @Override
  public List<CoinResponseDTO> getAllCoins() {
    ModelMapper modelMapper = new ModelMapper();
    List<CoinResponseDTO> coinResponseDTOS = new ArrayList<>();
    List<DBResponseDTO> dbResponseDTOS = fetchDBData();
    List<GeckoData> geckoDataList = geckoRepository.findAll();
    Map<String, GeckoData> geckoDataMap =
            geckoDataList.stream()
                    .collect(
                            Collectors.toMap(GeckoData::getId, geckoData-> geckoData));
    for (DBResponseDTO DBResponseDTO : dbResponseDTOS) {
      CoinResponseDTO coinResponseDTO =
              modelMapper.map(DBResponseDTO, CoinResponseDTO.class);
      GeckoData geckoData = geckoDataMap.get(coinResponseDTO.getApiId());
      coinResponseDTOS.add(mergeGeckoAndCoin(geckoData,coinResponseDTO));
    }
    return coinResponseDTOS;
  }

  @Override
  public ResponseEntity<CoinResponseDTO> getCoin(Integer id) {
    Coin coin = coinRepository.findById(id).orElse(null);
    if(coin != null){
      ModelMapper modelMapper = new ModelMapper();
      CoinResponseDTO coinResponseDTO = modelMapper.map(coin, CoinResponseDTO.class);
      Optional< GeckoData >geckoData = geckoRepository.findById(coinResponseDTO.getApiId());
      if (geckoData.isPresent()) {
        return ResponseEntity.ok(mergeGeckoAndCoin(geckoData.get(), coinResponseDTO));
      }
    }
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
  }

  private CoinResponseDTO  mergeGeckoAndCoin(GeckoData geckoData, CoinResponseDTO coinResponseDTO){
    ModelMapper modelMapper = new ModelMapper();
    modelMapper.typeMap(GeckoData.class, CoinResponseDTO.class).addMappings(mapper->{
      mapper.map(src->src.getId(),CoinResponseDTO::setApiId );
      mapper.skip(CoinResponseDTO::setId);
    });
    modelMapper.map(geckoData,coinResponseDTO);
    return  coinResponseDTO;
  }
}
