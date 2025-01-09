package service;

import dao.ExchangeRateDao;
import dao.JdbcExchangeRateDao;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import mapper.CreateExchangeRateMapper;
import mapper.ExchangeRateMapper;
import mapper.Mapper;
import mapper.PairCurrenciesMapper;
import model.dto.CreateExchangeRateDto;
import model.dto.CurrencyPairCodesDto;
import model.dto.ExchangeRateDto;
import model.entity.ExchangeRateEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExchangeRateService {
    private static final ExchangeRateService INSTANCE = new ExchangeRateService();
    private final ExchangeRateDao exchangeRateDao = JdbcExchangeRateDao.getInstance();
    private final ExchangeRateMapper exchangeRateMapper = (ExchangeRateMapper) ExchangeRateMapper.getInstance();
    private final Mapper<CreateExchangeRateDto, ExchangeRateEntity> createExchangeRateMapper = CreateExchangeRateMapper.getInstance();
    private final PairCurrenciesMapper pairCurrenciesMapper = PairCurrenciesMapper.getInstance();

    public List<ExchangeRateDto> findAll(){
        List<ExchangeRateDto> result = new ArrayList<>();
        for (ExchangeRateEntity exchangeRate : exchangeRateDao.findAll()) {
            result.add(exchangeRateMapper.mapFrom(exchangeRate));
        }
        return result;
    }

    public ExchangeRateDto save(CreateExchangeRateDto createExchangeRate) {
        ExchangeRateEntity exchangeRate = createExchangeRateMapper.mapFrom(createExchangeRate);
        ExchangeRateEntity save = exchangeRateDao.save(exchangeRate);
        return exchangeRateMapper.mapFrom(save);
    }

    public Optional<ExchangeRateDto> findByCodes(CurrencyPairCodesDto pair) {
        ExchangeRateEntity exchangeRate = pairCurrenciesMapper.mapFrom(pair);
        Optional<ExchangeRateEntity> maybeExchangeRate = exchangeRateDao.findByCodes(exchangeRate);
        return maybeExchangeRate.map(exchangeRateMapper::mapFrom);
    }

    public static ExchangeRateService getInstance() {
        return INSTANCE;
    }
}
