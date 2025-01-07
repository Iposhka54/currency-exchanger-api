package util;

import lombok.experimental.UtilityClass;
import org.sqlite.SQLiteConfig;

import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@UtilityClass
public class ConnectionManager {
    private static final SQLiteConfig CONFIG = new SQLiteConfig();
    static{
        SQLiteConfig CONFIG = new SQLiteConfig();
        CONFIG.enforceForeignKeys(true);
        CONFIG.setEncoding(SQLiteConfig.Encoding.UTF8);
        try{
            Connection conn = DriverManager.getConnection(getConnectionUrl(), CONFIG.toProperties());
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static Connection get() {
        try {
            Connection connection = DriverManager.getConnection(getConnectionUrl(), CONFIG.toProperties());
            connection.setAutoCommit(true);
            return connection;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String getConnectionUrl() {
        URL url = ConnectionManager.class.getClassLoader().getResource("currency_exchange.db");
        if (url != null) {
            try {
                return String.format("jdbc:sqlite:" + url.toURI());
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}