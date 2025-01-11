package model.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class CurrencyPairCodesDto {
    CurrencyDto base;
    CurrencyDto target;
    BigDecimal rate;
    BigDecimal amount;
}
