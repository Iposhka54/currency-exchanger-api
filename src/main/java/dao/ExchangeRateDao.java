package dao;

import model.dto.CurrencyPairCodesDto;
import model.entity.ExchangeRateEntity;

import java.util.Optional;

public interface ExchangeRateDao extends Dao<Integer, ExchangeRateEntity>{
    Optional<ExchangeRateEntity> findByCodes(ExchangeRateEntity dto);
}
