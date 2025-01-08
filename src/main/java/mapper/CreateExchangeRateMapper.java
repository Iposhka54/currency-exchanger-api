package mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import model.dto.CreateExchangeRateDto;
import model.dto.CurrencyDto;
import model.dto.ExchangeRateDto;
import model.entity.CurrencyEntity;
import model.entity.ExchangeRateEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateExchangeRateMapper implements Mapper<CreateExchangeRateDto, ExchangeRateEntity>{
    private static final Mapper<CreateExchangeRateDto, ExchangeRateEntity> INSTANCE = new CreateExchangeRateMapper();
    private final CurrencyMapper currencyMapper = (CurrencyMapper) CurrencyMapper.getInstance();

    @Override
    public ExchangeRateEntity mapFrom(CreateExchangeRateDto object) {
       return new ExchangeRateEntity(currencyMapper.mapTo(object.getBaseCurrency()),
                currencyMapper.mapTo(object.getTargetCurrency()),
                object.getRate());
    }

    public static Mapper<CreateExchangeRateDto, ExchangeRateEntity> getInstance() {
        return INSTANCE;
    }
}
