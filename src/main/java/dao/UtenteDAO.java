package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import entity.*;

public class UtenteDAO {
    public boolean salvaUtente(Utente utente){
        String sql = "INSERT INTO utente (username, password, matricola, email) VALUES (?, ?, ?, ?)";
        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql)){
            query.setString(1, utente.getUsername());
            query.setString(2, utente.getPassword());
            query.setString(3, utente.getMatricola());
            query.setString(4, utente.getEmail());

            int numModifiche = query.executeUpdate();
            return numModifiche > 0;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    public boolean modificaUtente(Utente utente){return true;}
    public boolean eliminaUtente(Utente utente){return true;}
    public Utente ottieniUtente(int id){return null;}
    public ArrayList<Utente> ottieniTuttiUtenti(){return null;}
}
