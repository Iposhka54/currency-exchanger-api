package model.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CurrencyDto {
    Integer id;
    @SerializedName("name")
    String fullName;
    String code;
    String sign;
}
