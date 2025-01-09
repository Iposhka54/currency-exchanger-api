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
                    base.id AS base_id,
                    base.code AS base_code,
                    base.full_name AS base_full_name,
                    base.sign AS base_sign,
                    target.id AS target_id,
                    target.code AS target_code,
                    target.sign AS target_sign,
                    target.full_name AS target_full_name,
                    ExchangeRates.rate AS rate
            FROM ExchangeRates
            JOIN Currencies AS base ON base_currency_id = base.id
            JOIN Currencies AS target ON target_currency_id = target.id
            """;
    private static final String SAVE_SQL = """
            INSERT INTO ExchangeRates(base_currency_id, target_currency_id, rate)
            VALUES (?, ?, ?);
            """;

    private static final String FIND_BY_CODES_SQL = FIND_ALL_SQL + """
            WHERE base.code = ? AND target.code = ?;
            """;

    @Override
    public Optional<ExchangeRateEntity> findByCodes(ExchangeRateEntity dto) {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_CODES_SQL);
        ){
            statement.setString(1, dto.getBaseCurrency().getCode());
            statement.setString(2, dto.getTargetCurrency().getCode());
            ResultSet resultSet = statement.executeQuery();
            Optional<ExchangeRateEntity> result = Optional.empty();
            if (resultSet.next()) {
                buildExchangeRate(dto, resultSet);
                result = Optional.of(dto);
            }
            return result;
        }catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private static void buildExchangeRate(ExchangeRateEntity dto, ResultSet resultSet) throws SQLException {
        dto.setRate(resultSet.getBigDecimal("rate"));
        dto.setId(resultSet.getInt("id"));
        dto.getBaseCurrency().setId(resultSet.getInt("base_id"));
        dto.getBaseCurrency().setFullName(resultSet.getString("base_full_name"));
        dto.getBaseCurrency().setSign(resultSet.getString("base_sign"));
        dto.getTargetCurrency().setId(resultSet.getInt("target_id"));
        dto.getTargetCurrency().setFullName(resultSet.getString("target_full_name"));
        dto.getTargetCurrency().setSign(resultSet.getString("target_sign"));
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
                JdbcCurrencyDao.getInstance().buildCurrency(resultSet, "base_"),
                JdbcCurrencyDao.getInstance().buildCurrency(resultSet, "target_"),
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
