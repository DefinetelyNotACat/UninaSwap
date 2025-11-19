package dao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgreSQLConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/uninaswap";
    private static final String UTENTE = "";
    private static final String PASSWORD = "";
    private static final String DRIVER = "org.postgresql.Driver";
    private static Connection connessione;

    static{
        try{
            Class.forName(DRIVER);
        }catch (ClassNotFoundException e){
            System.err.println("Driver non trovato: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public static Connection getConnection() throws SQLException{
        return DriverManager.getConnection(URL, UTENTE, PASSWORD);
    }
}
