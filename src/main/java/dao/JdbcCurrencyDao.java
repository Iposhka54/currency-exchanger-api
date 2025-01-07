package dao;

import exception.DaoException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import model.entity.CurrencyEntity;
import util.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    public Integer save(CurrencyEntity entity){
        return 0;
    }

    @Override
    public void update(CurrencyEntity entity){

    }

    @Override
    public void delete(CurrencyEntity entity){

    }

    public static JdbcCurrencyDao getInstance() {
        return INSTANCE;
    }

    CurrencyEntity buildCurrency(ResultSet resultSet) throws SQLException {
        return CurrencyEntity.builder()
                .id(resultSet.getObject("id", Integer.class))
                .fullName(resultSet.getObject("full_name", String.class))
                .code(resultSet.getObject("code", String.class))
                .sign(resultSet.getObject("sign", String.class))
                .build();
    }
}
