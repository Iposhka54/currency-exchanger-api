package util;

import lombok.experimental.UtilityClass;
import org.sqlite.SQLiteConfig;

import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@UtilityClass
public class ConnectionManager {
    private static final String DATABASE_URL = "jdbc:sqlite:C:\\work\\currency_exchange.db";
    private static final SQLiteConfig CONFIG = new SQLiteConfig();
    static{
        loadDriver();
        SQLiteConfig CONFIG = new SQLiteConfig();
        CONFIG.enforceForeignKeys(true);
        CONFIG.setEncoding(SQLiteConfig.Encoding.UTF8);
        try{
            Connection conn = DriverManager.getConnection(DATABASE_URL, CONFIG.toProperties());
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static Connection get() {
        try {
            Connection connection = DriverManager.getConnection(DATABASE_URL, CONFIG.toProperties());
            connection.setAutoCommit(true);
            return connection;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadDriver() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}