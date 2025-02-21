package dao;

import exception.CurrencyAlreadyExistsException;
import exception.DaoException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import model.entity.CurrencyEntity;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;
import util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JdbcCurrencyDao implements CurrencyDao {
    private static final JdbcCurrencyDao INSTANCE = new JdbcCurrencyDao();
    private static final String FIND_BY_CODE_SQL = """
            SELECT
                id,
                full_name,
                code,
                sign
                FROM Currencies
            WHERE code = ?;
            """;
    private static final String FIND_ALL_SQL = """
            SELECT
                id,
                full_name,
                code,
                sign
                FROM Currencies;
                """;

    private static final String SAVE_SQL = """
            INSERT INTO Currencies(full_name, code, sign)
            VALUES (?, ?, ?);
            """;

    @Override
    public Optional<CurrencyEntity> findByCode(String code){
        try(Connection connection = ConnectionManager.get();
        PreparedStatement statement = connection.prepareStatement(FIND_BY_CODE_SQL)
        ){
            statement.setString(1, code);
            ResultSet resultSet = statement.executeQuery();
            CurrencyEntity currency = null;
            if(resultSet.next()) {
                currency = buildCurrency(resultSet);
            }
            return Optional.ofNullable(currency);
        }catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Optional<CurrencyEntity> findById(Integer id){
        return Optional.empty();
    }

    @Override
    public List<CurrencyEntity> findAll(){
        try(Connection connection = ConnectionManager.get();
        PreparedStatement statement = connection.prepareStatement(FIND_ALL_SQL)
        ){
            ResultSet resultSet = statement.executeQuery();
            List<CurrencyEntity> currencies = new ArrayList<>();
            while(resultSet.next()) {
                currencies.add(buildCurrency(resultSet));
            }
            return currencies;
        }catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public CurrencyEntity save(CurrencyEntity entity){
        try(Connection connection = ConnectionManager.get();
        PreparedStatement statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)){
            statement.setString(1, entity.getFullName());
            statement.setString(2, entity.getCode());
            statement.setString(3, entity.getSign());
            statement.executeUpdate();
            ResultSet keys = statement.getGeneratedKeys();
            keys.next();
            entity.setId(keys.getObject(1, Integer.class));
            return entity;
        }catch (SQLException e){
            if (SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE.code == ((SQLiteException) e).getResultCode().code){
                throw new CurrencyAlreadyExistsException(e);
            }
            throw new DaoException(e);
        }
    }

    @Override
    public CurrencyEntity update(CurrencyEntity entity) {
        return null;
    }

    @Override
    public CurrencyEntity delete(CurrencyEntity entity) {
        return null;
    }

    public static JdbcCurrencyDao getInstance() {
        return INSTANCE;
    }

    public CurrencyEntity buildCurrency(ResultSet resultSet, String prefix) throws SQLException {
        return CurrencyEntity.builder()
                .id(resultSet.getInt(prefix + "id"))
                .fullName(resultSet.getString(prefix + "full_name"))
                .code(resultSet.getString(prefix + "code"))
                .sign(resultSet.getString(prefix + "sign"))
                .build();
    }

    public CurrencyEntity buildCurrency(ResultSet resultSet) throws SQLException {
        return buildCurrency(resultSet, "");
    }
}
