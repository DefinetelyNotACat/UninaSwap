package dao;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class PostgreSQLConnection {
    private static Connection connection;

    private PostgreSQLConnection() {

    }
    public static synchronized Connection getConnection() {
            Properties props = new Properties();
            try (InputStream input = getResourceAsStream("db.properties")) {
                if (input == null) {
                    throw new RuntimeException("db.properties file not found.");
                }
                props.load(input);
                String url = props.getProperty("db.url");
                String user = props.getProperty("db.user");
                String password = props.getProperty("db.password");
                connection = DriverManager.getConnection(url, user, password);
            } catch (IOException | SQLException e) {
                throw new RuntimeException("Error connecting to the database", e);
            }

        return connection;
    }

    private static InputStream getResourceAsStream(String fileName) {
        return PostgreSQLConnection.class.getClassLoader().getResourceAsStream(fileName);
    }
}
