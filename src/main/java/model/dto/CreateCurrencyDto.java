package model.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CreateCurrencyDto {
    String name;
    String code;
    String sign;
}
