package model.dto;

import lombok.Builder;
import lombok.Value;

import java.util.Currency;

@Value
@Builder
public class CurrencyPairCodesDto {
    CurrencyDto base;
    CurrencyDto target;
}
