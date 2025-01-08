package mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import model.dto.CreateCurrencyDto;
import model.dto.CurrencyDto;
import model.entity.CurrencyEntity;

import java.util.Currency;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateCurrencyMapper implements Mapper<CreateCurrencyDto, CurrencyEntity> {
    private static final Mapper<CreateCurrencyDto, CurrencyEntity> INSTANCE = new CreateCurrencyMapper();


    @Override
    public CurrencyEntity mapFrom(CreateCurrencyDto object) {
        if(object == null) return null;
        return CurrencyEntity.builder()
                .code(object.getCode())
                .fullName(object.getName())
                .sign(object.getSign())
                .build();
    }

    public static Mapper<CreateCurrencyDto, CurrencyEntity> getInstance(){
        return INSTANCE;
    }
}
