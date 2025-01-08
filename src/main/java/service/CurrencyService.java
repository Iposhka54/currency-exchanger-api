package service;

import dao.CurrencyDao;
import dao.JdbcCurrencyDao;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import mapper.CreateCurrencyMapper;
import mapper.CurrencyMapper;
import mapper.Mapper;
import model.dto.CreateCurrencyDto;
import model.dto.CurrencyDto;
import model.entity.CurrencyEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CurrencyService {
    private static final CurrencyService INSTANCE = new CurrencyService();
    private static final CurrencyDao currencyDao = JdbcCurrencyDao.getInstance();
    private static final Mapper<CurrencyEntity, CurrencyDto> currencyMapper = CurrencyMapper.getInstance();
    private static final Mapper<CreateCurrencyDto, CurrencyEntity> createCurrencyMapper = CreateCurrencyMapper.getInstance();

    public Optional<CurrencyDto> findByCode(String code){
        Optional<CurrencyEntity> currency = currencyDao.findByCode(code);
        return currency.map(currencyMapper::map);
    }

    public List<CurrencyDto> findAll(){
        List<CurrencyDto> currencies = new ArrayList<>();
        for (CurrencyEntity currency : currencyDao.findAll()) {
            currencies.add(currencyMapper.map(currency));
        }
        return currencies;
    }

    public CurrencyDto save(CreateCurrencyDto createCurrency){
        CurrencyEntity currency = createCurrencyMapper.map(createCurrency);
        CurrencyEntity resultEntity = currencyDao.save(currency);
        return currencyMapper.map(resultEntity);
    }
    public static CurrencyService getInstance(){
        return INSTANCE;
    }
}
