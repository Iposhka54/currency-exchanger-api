package mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import model.dto.CurrencyPairCodesDto;
import model.entity.ExchangeRateEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PairCurrenciesMapper implements Mapper<CurrencyPairCodesDto, ExchangeRateEntity> {
    private static final PairCurrenciesMapper INSTANCE = new PairCurrenciesMapper();
    private final CurrencyMapper currencyMapper = (CurrencyMapper) CurrencyMapper.getInstance();
    @Override
    public ExchangeRateEntity mapFrom(CurrencyPairCodesDto object) {
        return new ExchangeRateEntity(currencyMapper.mapToOnlyCode(object.getBase()), currencyMapper.mapToOnlyCode(object.getTarget()));
    }

    public static PairCurrenciesMapper getInstance() {
        return INSTANCE;
    }
}
