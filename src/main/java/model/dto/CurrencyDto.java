package model.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public final class CurrencyDto {
    Integer id;
    String code;
    @SerializedName("name")
    String fullName;
    String sign;
}
