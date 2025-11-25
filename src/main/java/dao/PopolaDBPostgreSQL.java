package dao;

import java.sql.Connection;
import java.sql.Statement;

public class PopolaDBPostgreSQL {
    public static void creaDB(){
        try(Connection conn = PostgreSQLConnection.getConnection();
            Statement stmt = conn.createStatement();){
            String queryUtente = "CREATE TABLE IF NOT EXISTS UTENTE (" +
                    "email VARCHAR(100) PRIMARY KEY, " +
                    "username VARCHAR(50) UNIQUE NOT NULL, " +
                    "password VARCHAR(255) NOT NULL, " +
                    "matricola VARCHAR" +
                    "(20) UNIQUE NOT NULL" +
                    ");";
        }
        catch (Exception e){

        }
    }
}
