package com.example.uninaswap.dao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import io.github.cdimascio.dotenv.Dotenv;
public class PostgreSQLConnection {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String URL = dotenv.get("DB_URL");
    private static final String UTENTE = dotenv.get("DB_USER");;
    private static final String PASSWORD = dotenv.get("DB_PASSWORD");
    private static final String DRIVER = "org.postgresql.Driver";

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
