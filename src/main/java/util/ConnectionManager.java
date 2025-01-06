package util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.experimental.UtilityClass;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;

@UtilityClass
public class ConnectionManager {
    private static final String URL_KEY = "db.url";
    private static final HikariDataSource DATA_SOURCE;
    static {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(PropertiesUtil.getProperty(URL_KEY));
            config.setMaximumPoolSize(100);
            config.setMinimumIdle(5);
            config.setConnectionTimeout(30000);
            DATA_SOURCE = new HikariDataSource(config);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize SQLite DataSource", e);
        }
    }


    public static Connection get() {
        try {
            return DATA_SOURCE.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}