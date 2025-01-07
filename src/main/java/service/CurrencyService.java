package service;

import dao.CurrencyDao;
import dao.JdbcCurrencyDao;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import mapper.CurrencyMapper;
import model.dto.CurrencyDto;
import model.entity.CurrencyEntity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CurrencyService {
    private static final CurrencyService INSTANCE = new CurrencyService();
    private static final CurrencyDao currencyDao = JdbcCurrencyDao.getInstance();
    private static final CurrencyMapper currencyMapper = CurrencyMapper.getInstance();

    public CurrencyDto findByCode(String code) throws SQLException {
        Optional<CurrencyEntity> currency = currencyDao.findByCode(code);
        return currencyMapper.map(currency.get());
    }

    public List<CurrencyDto> findAll() throws SQLException {
        List<CurrencyDto> currencies = new ArrayList<>();
        for (CurrencyEntity currency : currencyDao.findAll()) {
            currencies.add(currencyMapper.map(currency));
        }
        return currencies;
    }

    public static CurrencyService getInstance(){
        return INSTANCE;
    }
}
