package util;

import lombok.experimental.UtilityClass;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@UtilityClass
public class ConnectionManager {

    public static Connection get() {
        Connection connection = null;
        try {
            URL dbPath = ConnectionManager.class.getClassLoader().getResource("currency_exchange.db");
            String path = null;
            try {
                path = new File(dbPath.toURI()).getAbsolutePath();
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }

            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(String.format("jdbc:sqlite:%s", path));

        } catch (SQLException e) {
            System.out.println("Connection FAIL");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }
}