package mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import model.dto.CurrencyDto;
import model.entity.CurrencyEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CurrencyMapper implements Mapper<CurrencyEntity, CurrencyDto>{
    private static final Mapper<CurrencyEntity, CurrencyDto> INSTANCE = new CurrencyMapper();
    @Override
    public CurrencyDto mapFrom(CurrencyEntity object) {
        if(object == null)return null;
        return CurrencyDto.builder()
                .id(object.getId())
                .code(object.getCode())
                .fullName(object.getFullName())
                .sign(object.getSign())
                .build();
    }

    public CurrencyEntity mapTo(CurrencyDto object) {
        if(object == null)return null;
        return CurrencyEntity.builder()
                .id(object.getId())
                .code(object.getCode())
                .fullName(object.getFullName())
                .sign(object.getSign())
                .build();
    }


    public static Mapper<CurrencyEntity, CurrencyDto> getInstance() {
        return INSTANCE;
    }
}
