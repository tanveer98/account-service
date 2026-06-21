package org.example.accountservice.application;

import org.example.accountservice.domain.Currency;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class RateService {

    private static final Map<CurrencyPair, BigDecimal> RATES = Map.of(
            new CurrencyPair(Currency.EUR, Currency.USD), new BigDecimal("1.22"),
            new CurrencyPair(Currency.USD, Currency.EUR), new BigDecimal("0.82")
    );

    public BigDecimal getRate(Currency source, Currency target) {
        var pair = new CurrencyPair(source, target);
        return RATES.getOrDefault(pair, BigDecimal.ONE);
    }

    private record CurrencyPair(Currency source, Currency target) {}

}
