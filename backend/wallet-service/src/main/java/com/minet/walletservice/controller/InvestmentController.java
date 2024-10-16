package com.minet.walletservice.controller;
import com.minet.walletservice.dto.InvestmentHistory;
import com.minet.walletservice.dto.InvestmentResponse;
import com.minet.walletservice.entity.Investment;
import com.minet.walletservice.service.InvestmentService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@Slf4j
@Data
@RequestMapping("/api/wallets/investments")
public class InvestmentController {
    @Autowired
    private InvestmentService investmentService;

    @GetMapping
    public ResponseEntity<?> getInvestmentsByUserID(
           @RequestParam(value = "userId", required = true) Integer userID
    ) {
        log.debug(userID.toString());
        try {
            if (userID != null) {
                Optional<List<Investment>> investment = investmentService.getInvestmentByUserId(userID);
                if(investment.isPresent()) {
                    Map<Date,Double> dateInvestmentMap= new HashMap<>();
                    List<InvestmentHistory> historyArray = new ArrayList<>();
                            for(int i = 0; i<investment.get().size(); i++){
                                double investedAmount = investment.get().get(i).getAmount();
                                Date date = investment.get().get(i).getDate();
                                Double existingEntry=dateInvestmentMap.get(date);
                                if(existingEntry != null){
                                    dateInvestmentMap.put(date,existingEntry+investedAmount);
                                }
                                else{
                                    dateInvestmentMap.put(date,investedAmount);
                                }
                            }
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    for (Map.Entry<Date,Double> entry : dateInvestmentMap.entrySet()){
                        historyArray.add(new InvestmentHistory(simpleDateFormat.format(entry.getKey()),entry.getValue()));
                    }
                    InvestmentResponse investmentResponse = new InvestmentResponse(userID, "$", historyArray);
                    return ResponseEntity.ok(investmentResponse);
                }
            }
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }

      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected Error");
    }

}