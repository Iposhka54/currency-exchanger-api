package dao;

import exception.DaoException;
import exception.ExchangeRateAlreadyExistsException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import model.entity.CurrencyEntity;
import model.entity.ExchangeRateEntity;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;
import util.ConnectionManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JdbcExchangeRateDao implements ExchangeRateDao {
    private static final ExchangeRateDao INSTANCE = new JdbcExchangeRateDao();
    private final JdbcCurrencyDao currencyDao = JdbcCurrencyDao.getInstance();
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

    private static final String FIND_BY_CODES_REVERSE_SQL = FIND_ALL_SQL + """
            WHERE target.code = ? AND base.code = ?;
            """;

    private static final String UPDATE_SQL = """
            UPDATE ExchangeRates
                SET rate = ?
                WHERE base_currency_id = ? AND target_currency_id = ?
                RETURNING id;
            """;

    private static final String CROSS_SQL = """
            SELECT
                base.id AS base_id,
                base.full_name AS base_full_name,
                base.code AS base_code,
                base.sign AS base_sign,
                target.id AS target_id,
                target.full_name AS target_full_name,
                target.code AS target_code,
                target.sign AS target_sign,
                e_target.rate / e_base.rate AS rate
            FROM
                ExchangeRates AS e_base,
                ExchangeRates AS e_target
            JOIN Currencies AS base
            ON e_base.target_currency_id = base.id
            Join Currencies AS target
                    ON e_target.target_currency_id = target.id
            JOIN Currencies AS cross_
                 ON e_base.base_currency_id = cross_.id
            WHERE cross_.code = ? AND base.code = ? AND target.code = ?;
            """;
    private static final String CROSS_CODE = "USD";

    @Override
    public Optional<ExchangeRateEntity> findByCodes(ExchangeRateEntity dto) {
        return findByCodes(dto, FIND_BY_CODES_SQL);
    }

    public Optional<ExchangeRateEntity> findByCodes(ExchangeRateEntity dto, String sql){
        try (Connection connection = ConnectionManager.get();
             PreparedStatement statement = connection.prepareStatement(sql);
        ){
            Optional<CurrencyEntity> base = currencyDao.findByCode(dto.getBaseCurrency().getCode());
            Optional<CurrencyEntity> target = currencyDao.findByCode(dto.getTargetCurrency().getCode());
            if(base.isEmpty() || target.isEmpty()){
                return Optional.empty();
            }
            statement.setString(1, dto.getBaseCurrency().getCode());
            statement.setString(2, dto.getTargetCurrency().getCode());
            ResultSet resultSet = statement.executeQuery();
            Optional<ExchangeRateEntity> result = Optional.empty();
            if (resultSet.next()) {
                boolean reverse = sql.equals(FIND_BY_CODES_REVERSE_SQL);
                buildExchangeRate(dto, resultSet, reverse);
                result = Optional.of(dto);
            }
            return result;
        }catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private static void buildExchangeRate(ExchangeRateEntity dto, ResultSet resultSet, boolean reverse) throws SQLException {
        if (!reverse) {
            dto.setRate(resultSet.getBigDecimal("rate"));
            dto.setId(resultSet.getInt("id"));
            dto.getBaseCurrency().setId(resultSet.getInt("base_id"));
            dto.getBaseCurrency().setFullName(resultSet.getString("base_full_name"));
            dto.getBaseCurrency().setSign(resultSet.getString("base_sign"));
            dto.setTargetCurrency(new CurrencyEntity(
                    resultSet.getInt("target_id"),
                    resultSet.getString("target_full_name"),
                    resultSet.getString("target_code"),
                    resultSet.getString("target_sign")
            ));
        } else {
            dto.setRate(BigDecimal.ONE.divide(resultSet.getBigDecimal("rate"), 6, RoundingMode.HALF_UP));
            dto.setId(resultSet.getInt("id"));
            dto.setBaseCurrency(new CurrencyEntity(
                    resultSet.getInt("target_id"),
                    resultSet.getString("target_full_name"),
                    resultSet.getString("target_code"),
                    resultSet.getString("target_sign")
            ));
            dto.setTargetCurrency(new CurrencyEntity(
                    resultSet.getInt("base_id"),
                    resultSet.getString("base_full_name"),
                    resultSet.getString("base_code"),
                    resultSet.getString("base_sign")
            ));
        }
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

    public Optional<ExchangeRateEntity> exchange(ExchangeRateEntity entity) {
        Optional<ExchangeRateEntity> maybeExchangeRate = findByCodes(entity, FIND_BY_CODES_SQL);
        if(maybeExchangeRate.isPresent()) {
            return maybeExchangeRate;
        }
        maybeExchangeRate = findByCodes(entity, FIND_BY_CODES_REVERSE_SQL);
        if(maybeExchangeRate.isPresent()) {
            return maybeExchangeRate;
        }
        return crossExchange(entity);
    }

    private Optional<ExchangeRateEntity> crossExchange(ExchangeRateEntity entity) {
        try (Connection connection = ConnectionManager.get();
            PreparedStatement statement = connection.prepareStatement(CROSS_SQL)
        ){
            statement.setString(1, CROSS_CODE);
            statement.setString(2, entity.getBaseCurrency().getCode());
            statement.setString(3, entity.getTargetCurrency().getCode());
            ResultSet resultSet = statement.executeQuery();
            Optional<ExchangeRateEntity> result = Optional.empty();
            if(resultSet.next()) {
                result = Optional.of(new ExchangeRateEntity(
                        currencyDao.buildCurrency(resultSet, "base_"),
                        currencyDao.buildCurrency(resultSet, "target_"),
                        resultSet.getBigDecimal("rate")
                ));
            }
            return result;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public ExchangeRateEntity update(ExchangeRateEntity entity) {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)
        ){
            CurrencyEntity base = currencyDao.findByCode(entity.getBaseCurrency().getCode()).orElseThrow();
            CurrencyEntity target = currencyDao.findByCode(entity.getTargetCurrency().getCode()).orElseThrow();
            entity.setBaseCurrency(base);
            entity.setTargetCurrency(target);

            statement.setBigDecimal(1, entity.getRate());
            statement.setInt(2, entity.getBaseCurrency().getId());
            statement.setInt(3, entity.getTargetCurrency().getId());
            ResultSet rs = statement.executeQuery();
            ExchangeRateEntity result = null;
            if (rs.next()) {
                result = new ExchangeRateEntity(
                        rs.getInt("id"),
                        entity.getBaseCurrency(),
                        entity.getTargetCurrency(),
                        entity.getRate()
                );
            }
            return result;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public ExchangeRateEntity delete(ExchangeRateEntity entity) {
        return null;
    }

    public static ExchangeRateDao getInstance() {
        return INSTANCE;
    }
}
