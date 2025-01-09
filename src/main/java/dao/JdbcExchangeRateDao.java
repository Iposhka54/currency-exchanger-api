package dao;

import exception.DaoException;
import exception.ExchangeRateAlreadyExistsException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import model.dto.CurrencyPairCodesDto;
import model.entity.ExchangeRateEntity;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;
import util.ConnectionManager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JdbcExchangeRateDao implements ExchangeRateDao {
    private static final ExchangeRateDao INSTANCE = new JdbcExchangeRateDao();
    private static final String FIND_ALL_SQL = """
            SELECT 
                ExchangeRates.id AS id,
                base.id,
                base.code,
                base.full_name,
                base.sign,
                target.id,
                target.code,
                target.sign,
                ExchangeRates.rate AS rate
            
            FROM ExchangeRates
            JOIN Currencies AS base
            ON base_currency_id = base.id
            JOIN Currencies AS target
            ON target_currency_id = target.id;
            """;
    private static final String SAVE_SQL = """
            INSERT INTO ExchangeRates(base_currency_id, target_currency_id, rate)
            VALUES (?, ?, ?);
            """;
    @Override
    public Optional<ExchangeRateEntity> findByCodes(CurrencyPairCodesDto dto) {
        return Optional.empty();
    }

    @Override
    public Optional<ExchangeRateEntity> findById(Integer id) {
        return Optional.empty();
    }

    @Override
    public List<ExchangeRateEntity> findAll() {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_SQL);
        ){
            ResultSet resultSet = statement.executeQuery();
            List<ExchangeRateEntity> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(buildExchangeRate(resultSet));
            }
            return result;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public ExchangeRateEntity buildExchangeRate(ResultSet resultSet) throws SQLException {
        return new ExchangeRateEntity(
                resultSet.getObject("id", Integer.class),
                JdbcCurrencyDao.getInstance().buildCurrency(resultSet),
                JdbcCurrencyDao.getInstance().buildCurrency(resultSet),
                resultSet.getObject("rate", BigDecimal.class)
        );
    }

    @Override
    public ExchangeRateEntity save(ExchangeRateEntity entity) {
        try (Connection connection = ConnectionManager.get();
        PreparedStatement statement = connection.prepareStatement(SAVE_SQL, PreparedStatement.RETURN_GENERATED_KEYS)){

            statement.setInt(1, entity.getBaseCurrency().getId());
            statement.setInt(2, entity.getTargetCurrency().getId());
            statement.setBigDecimal(3, entity.getRate());

            statement.executeUpdate();

            ResultSet resultSet = statement.getGeneratedKeys();
            resultSet.next();
            entity.setId(resultSet.getObject(1, Integer.class));
            return entity;
        }catch (SQLException e) {
            if (SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE.code == ((SQLiteException) e).getResultCode().code){
                throw new ExchangeRateAlreadyExistsException(e);
            }
            throw new DaoException(e);
        }
    }

    @Override
    public void update(ExchangeRateEntity entity) {

    }

    @Override
    public void delete(ExchangeRateEntity entity) {

    }

    public static ExchangeRateDao getInstance() {
        return INSTANCE;
    }
}
