package model.entity;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class ExchangeRateEntity {
    private Integer id;
    private @NonNull CurrencyEntity baseCurrency;
    private @NonNull CurrencyEntity targetCurrency;
    private @NonNull BigDecimal rate;

    public ExchangeRateEntity(CurrencyEntity baseCurrency, CurrencyEntity targetCurrency, BigDecimal rate) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
    }
}