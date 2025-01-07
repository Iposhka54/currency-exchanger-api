package dao;

import model.entity.CurrencyEntity;

import java.sql.SQLException;
import java.util.Optional;

public interface CurrencyDao extends Dao<Integer, CurrencyEntity>{
    Optional<CurrencyEntity> findByCode(String code);
}
