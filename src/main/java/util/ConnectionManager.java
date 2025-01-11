package util;

import com.zaxxer.hikari.HikariDataSource;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@UtilityClass
public class ConnectionManager {
    public static final String SQLITE_DRIVER = "org.sqlite.JDBC";
    public static final String SQLITE_PREFIX = "jdbc:sqlite:";
    public static final HikariDataSource DATASOURCE = new HikariDataSource();

    static{
        URL dbPath = ConnectionManager.class.getClassLoader().getResource("currency_exchange.db");
        String path = null;
        try {
            path = new File(dbPath.toURI()).getAbsolutePath();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        DATASOURCE.setDriverClassName(SQLITE_DRIVER);
        DATASOURCE.setJdbcUrl(SQLITE_PREFIX + path);
    }

    public static Connection get() throws SQLException {
        return DATASOURCE.getConnection();
    }
}