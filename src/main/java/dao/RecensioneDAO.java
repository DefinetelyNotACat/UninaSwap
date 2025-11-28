package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import entity.*;
import interfaces.GestoreCategoria;
import interfaces.GestoreRecensione;

public class RecensioneDAO implements GestoreRecensione {
    public Recensione OttieniRecensione(int id) {
        Recensione recensione = null;
        String sql = "SELECT * FROM RECENSIONE WHERE id = ?";
            try (Connection connessione = PostgreSQLConnection.getConnection();
        PreparedStatement query = connessione.prepareStatement(sql);) {
            query.setInt(1, id);
            try (ResultSet rs = query.executeQuery()) {
                if (rs.next()) {
                    recensione = new Recensione((rs.getInt("Voto")));
                    recensione.setCommento(rs.getString("commento"));
                    recensione.setId(id);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
            return recensione;
    }
    public List<Recensione> OttieniTutteRecensione(){
        ArrayList<Recensione> tutteRecensione= new ArrayList<>();
        String sql = "SELECT * FROM RECENSIONE";
        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql);
             ResultSet rs = query.executeQuery()) {
            while (rs.next()) {
                Recensione recensione = new Recensione((rs.getInt("voto")));
                recensione.setCommento(rs.getString("commento"));
                recensione.setId(rs.getInt("id"));
                tutteRecensione.add(recensione);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tutteRecensione;
    }

    public boolean SalvaRecensione(Recensione recensione){
        String sql = "INSERT INTO RECENSIONE (Voto, commento) VALUES ( ?, ?)";
        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql)) {
            query.setInt(1, recensione.getVoto());
            query.setString(2, recensione.getCommento());
            int numModifiche = query.executeUpdate();
            return numModifiche > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean ModificaRecensione(Recensione recensione){
        String sql = "UPDATE RECENSIONE SET Voto = ?, commento = ? WHERE id = ?";
        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql)) {
            query.setInt(1, recensione.getVoto());
            query.setString(2, recensione.getCommento());
            query.setInt(3, recensione.getId());

            int numModifiche = query.executeUpdate();
            return numModifiche > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean EliminaRecensione(int id){
        String sql = "DELETE FROM RECENSIONE WHERE id = ?";
        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql)) {

            query.setInt(1, id);

            int numModifiche = query.executeUpdate();
            return numModifiche > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
