package mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import model.dto.CurrencyDto;
import model.dto.ExchangeRateDto;
import model.entity.CurrencyEntity;
import model.entity.ExchangeRateEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExchangeRateMapper implements Mapper<ExchangeRateEntity, ExchangeRateDto>{
    private static final Mapper<ExchangeRateEntity, ExchangeRateDto> INSTANCE = new ExchangeRateMapper();
    private final CurrencyMapper currencyMapper = (CurrencyMapper) CurrencyMapper.getInstance();
    @Override
    public ExchangeRateDto mapFrom(ExchangeRateEntity object) {
        if(object == null) return null;
        return ExchangeRateDto.of(object.getId(),
                currencyMapper.mapFrom(object.getBaseCurrency()),
                currencyMapper.mapFrom(object.getTargetCurrency()),
                object.getRate());
    }

    public ExchangeRateEntity mapTo(ExchangeRateDto object) {
        if(object == null) return null;
        return new ExchangeRateEntity(
                currencyMapper.mapTo(object.getBaseCurrency()),
                currencyMapper.mapTo(object.getTargetCurrency()),
                object.getRate()
        );
    }

    public ExchangeRateEntity mapToOnlyCode(ExchangeRateDto object) {
        if(object == null) return null;
        return new ExchangeRateEntity(
                currencyMapper.mapToOnlyCode(object.getBaseCurrency()),
                currencyMapper.mapToOnlyCode(object.getTargetCurrency()),
                object.getRate()
        );
    }

    public static Mapper<ExchangeRateEntity, ExchangeRateDto> getInstance() {
        return INSTANCE;
    }
}
