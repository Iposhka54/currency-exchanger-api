package mapper;

import exception.EntityMapException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import model.dto.CurrencyDto;
import model.entity.CurrencyEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CurrencyMapper implements Mapper<CurrencyEntity, CurrencyDto>{
    private static final CurrencyMapper INSTANCE = new CurrencyMapper();
    @Override
    public CurrencyDto map(CurrencyEntity object) {
        if(object == null)throw new EntityMapException();
        return CurrencyDto.builder()
                .id(object.getId())
                .code(object.getCode())
                .fullName(object.getFullName())
                .sign(object.getSign())
                .build();
    }

    public static CurrencyMapper getInstance() {
        return INSTANCE;
    }
}
