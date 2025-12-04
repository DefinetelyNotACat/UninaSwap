package com.example.uninaswap.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.example.uninaswap.entity.Recensione;
import com.example.uninaswap.interfaces.GestoreRecensioneDAO;

public class RecensioneDAO implements GestoreRecensioneDAO {
    public Recensione OttieniRecensione(int id) {
        Recensione recensione = null;
        String sql = "SELECT * FROM RECENSIONE WHERE id = ?";
        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql)) {

            query.setInt(1, id);
            try (ResultSet rs = query.executeQuery()) {
                if (rs.next()) {
                    recensione = new Recensione(
                            rs.getString("Recensito"),
                            rs.getString("Recensore"),
                            rs.getInt("voto")
                    );
                    recensione.setCommento(rs.getString("commento"));
                    recensione.setId(id);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return recensione;
    }

    public List<Recensione> OttieniTutteRecensione() {
        ArrayList<Recensione> tutteRecensione = new ArrayList<>();
        String sql = "SELECT * FROM RECENSIONE";
        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql);
             ResultSet rs = query.executeQuery()) {

            while (rs.next()) {
                Recensione recensione = new Recensione(
                        rs.getString("Recensito"),
                        rs.getString("Recensore"),
                        rs.getInt("voto")
                );
                recensione.setCommento(rs.getString("commento"));
                recensione.setId(rs.getInt("id"));
                tutteRecensione.add(recensione);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tutteRecensione;
    }

    public boolean SalvaRecensione(Recensione recensione) {
        String sql = "INSERT INTO RECENSIONE (Voto, commento, Recensore, Recensito) VALUES (?, ?, ?, ?)";
        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql)) {
            query.setInt(1, recensione.getVoto());
            query.setString(2, recensione.getCommento());
            query.setString(3, recensione.getRecensore());
            query.setString(4, recensione.getRecensito());

            int numModifiche = query.executeUpdate();
            return numModifiche > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean ModificaRecensione(Recensione recensione) {
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

    public boolean EliminaRecensione(int id) {
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