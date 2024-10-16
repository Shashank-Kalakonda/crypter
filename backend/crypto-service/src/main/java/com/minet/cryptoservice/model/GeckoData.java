package com.minet.cryptoservice.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "geckos")
public class GeckoData {
    @Id
    private String id;
    private Double currentPrice;

    private BigDecimal marketCap;

    private Float priceChangePercentage24h;

    private Double circulatingSupply;

    private Double volume;
}
