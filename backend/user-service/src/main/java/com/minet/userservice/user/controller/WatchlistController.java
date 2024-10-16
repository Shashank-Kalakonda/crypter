package com.minet.userservice.user.controller;

import com.minet.userservice.user.dto.WatchlistDto;
import com.minet.userservice.user.entity.Watchlist;
import com.minet.userservice.user.service.WatchlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class WatchlistController {

    @Autowired
    private WatchlistService watchlistService;

    @GetMapping("/watchlist")
    public ResponseEntity<List<Watchlist>> getAllWatchlistByUserId(@RequestParam Long userId){
        return new ResponseEntity<>(watchlistService.getAllWatchlist(userId), HttpStatus.OK);
    }

    @PostMapping("/watchlist")
    public ResponseEntity<String> addWatchlistToUser(@RequestBody WatchlistDto watchlistDto){
        Watchlist watchlist=new Watchlist();
        watchlist.setUserId(watchlistDto.getUserId());
        watchlist.setCoinId(watchlistDto.getCoinId());
        return new ResponseEntity<>(watchlistService.addWatchlistToUser(watchlist),HttpStatus.CREATED);
    }

    @DeleteMapping("/watchlist")
    public ResponseEntity<String> deleteWatchlistOfUser(@RequestBody WatchlistDto watchlistDto){
        WatchlistDto watchlist=new WatchlistDto();
        watchlist.setCoinId(watchlistDto.getCoinId());
        watchlist.setUserId(watchlistDto.getUserId());

        return new ResponseEntity<>(watchlistService.deleteWatchlistOfUser(watchlist),HttpStatus.OK);
    }
}
