package com.minet.walletservice.service;
import com.minet.walletservice.dto.*;
import com.minet.walletservice.entity.Coin;
import com.minet.walletservice.entity.Wallet;
import com.minet.walletservice.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public  class WalletServicesImpl implements WalletService {
    @Autowired
    WalletRepository walletRepository;
    @Autowired
    RestTemplate restTemplate;

    @Override
    public WalletIDResponseDto findWalletById(int walletId, WalletIDRequestDto walletIDRequestDto) {
        Optional<Wallet> walletEntityOpt = walletRepository.findById(walletId);
        if (walletEntityOpt.isPresent()) {
            Wallet walletEntity = walletEntityOpt.get();
            WalletIDResponseDto walletIDResponseDto = new WalletIDResponseDto();

            walletIDResponseDto.setId(walletEntity.getId());
            walletIDResponseDto.setCoinId(walletEntity.getCoinId());
            walletIDResponseDto.setAmount(walletEntity.getBalance() + walletIDRequestDto.getAmount());

            walletEntity.setBalance(walletIDResponseDto.getAmount());
            walletRepository.save(walletEntity);
            return walletIDResponseDto;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet not found");
        }
    }
    @Override
    public WalletResponceDto createData(WalletRequestDto walletBody) {
        Wallet existingWallet = walletRepository.findByUserIdAndCoinId(walletBody.getUserId(),walletBody.getCoinId());

        if (existingWallet!=null) {
            existingWallet.setBalance(walletBody.getAmount());
            walletRepository.save(existingWallet);
            WalletResponceDto walletResponceDto = new WalletResponceDto();
            walletResponceDto.setId(existingWallet.getId());
            walletResponceDto.setCoinId(existingWallet.getCoinId());
            walletResponceDto.setUserId(existingWallet.getUserId());
            walletResponceDto.setAmount(existingWallet.getBalance());
            return walletResponceDto;
        } else {
            Wallet newWallet = new Wallet();
            newWallet.setUserId(walletBody.getUserId());
            newWallet.setCoinId(walletBody.getCoinId());
            newWallet.setBalance(walletBody.getAmount());
            walletRepository.save(newWallet);

            WalletResponceDto walletResponceDto = new WalletResponceDto();
            walletResponceDto.setId(newWallet.getId());
            walletResponceDto.setCoinId(newWallet.getCoinId());
            walletResponceDto.setUserId(newWallet.getUserId());
            walletResponceDto.setAmount(newWallet.getBalance());
            return walletResponceDto;
        }
    }
    @Override
    public List<UserWalletDTO> userWalletDetails(int userId ) {
        List<Wallet> walletEntities = walletRepository.findByUserId(userId);
        if (walletEntities.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet not found with the given userId");
        }

        return walletEntities.stream()
                .map(this::convertentitytoDTO)
                .collect(Collectors.toList());
    }
    public  UserWalletDTO convertentitytoDTO(Wallet walletEntity){
        UserWalletDTO userWalletDTO=new UserWalletDTO();
        userWalletDTO.setWalletId(walletEntity.getId());
        userWalletDTO.setCoinId(walletEntity.getCoinId());
        userWalletDTO.setUserId(walletEntity.getUserId());
        userWalletDTO.setAmount(walletEntity.getBalance());
        String apiUrl = "http://CRYPTO-SERVICE/api/cryptos/" + walletEntity.getCoinId();
        ResponseEntity<Coin> responseEntity = restTemplate.exchange(
                apiUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        Coin coins = responseEntity.getBody();
        if (coins != null) {
            userWalletDTO.setName(coins.getName());
            userWalletDTO.setAcronym(coins.getAcronym());
        }
        return  userWalletDTO;
    }
    @Override
    public List<PortfolioResponseDto> findPortfolioDetails(int userId) {
        List<Wallet> walletEntities = walletRepository.findByUserId(userId);
        if (walletEntities.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return    walletEntities.stream()
                .map(this::convertentitytoDTOS)
                .collect(Collectors.toList());
    }
    public  PortfolioResponseDto convertentitytoDTOS(Wallet walletEntity){
        PortfolioResponseDto portfolioResponseDto=new PortfolioResponseDto();
        portfolioResponseDto.setPortfolioId(walletEntity.getId());
        portfolioResponseDto.setCoinId(walletEntity.getCoinId());
        portfolioResponseDto.setBalance(walletEntity.getBalance());
        String apiUrl = "http://CRYPTO-SERVICE/api/cryptos/" + walletEntity.getCoinId();
        ResponseEntity<Coin> responseEntity = restTemplate.exchange(
                apiUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        Coin coins = responseEntity.getBody();
        if (coins != null) {
            portfolioResponseDto.setAcronym(coins.getAcronym());
            portfolioResponseDto.setName(coins.getName());
            portfolioResponseDto.setIconUrl(coins.getIconUrl());
        }

        return portfolioResponseDto;
    }

}
