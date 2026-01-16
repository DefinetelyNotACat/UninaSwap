package com.example.uninaswap.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.example.uninaswap.entity.Recensione;
import com.example.uninaswap.interfaces.GestoreRecensioneDAO;

public class RecensioneDAO implements GestoreRecensioneDAO {

    // Query con JOIN per recuperare le email partendo dagli ID presenti nella tabella recensione
    private static final String SELECT_BASE =
            "SELECT r.*, u1.email AS email_recensore, u2.email AS email_recensito " +
                    "FROM recensione r " +
                    "JOIN utente u1 ON r.recensore_id = u1.id " +
                    "JOIN utente u2 ON r.recensito_id = u2.id";

    public Recensione OttieniRecensione(int id) {
        Recensione recensione = null;
        String sql = SELECT_BASE + " WHERE r.id = ?";

        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql)) {

            query.setInt(1, id);
            try (ResultSet rs = query.executeQuery()) {
                if (rs.next()) {
                    // Utilizziamo gli alias definiti nella JOIN per popolare l'entità
                    recensione = new Recensione(
                            rs.getString("email_recensito"),
                            rs.getString("email_recensore"),
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
        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(SELECT_BASE);
             ResultSet rs = query.executeQuery()) {

            while (rs.next()) {
                Recensione recensione = new Recensione(
                        rs.getString("email_recensito"),
                        rs.getString("email_recensore"),
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
        // Correzione nomi colonne: recensore_id e recensito_id
        // Utilizziamo una subquery per trovare l'ID dell'utente partendo dall'email dell'entità
        String sql = "INSERT INTO recensione (voto, commento, recensore_id, recensito_id) " +
                "VALUES (?, ?, (SELECT id FROM utente WHERE email = ?), (SELECT id FROM utente WHERE email = ?))";

        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql)) {

            query.setInt(1, recensione.getVoto());
            query.setString(2, recensione.getCommento());
            query.setString(3, recensione.getRecensore()); // Passa l'email
            query.setString(4, recensione.getRecensito()); // Passa l'email

            int numModifiche = query.executeUpdate();
            return numModifiche > 0;

        } catch (Exception e) {
            System.err.println("Errore nel salvataggio: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean ModificaRecensione(Recensione recensione) {
        String sql = "UPDATE recensione SET voto = ?, commento = ? WHERE id = ?";
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
        String sql = "DELETE FROM recensione WHERE id = ?";
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
    public ArrayList<Recensione> ottieniRecensioniPerUtente(String emailRecensito) {
        ArrayList<Recensione> recensioni = new ArrayList<>();
        // Query che filtra per l'utente che ha RICEVUTO la recensione
        String sql = "SELECT r.*, u1.email AS email_recensore, u2.email AS email_recensito " +
                "FROM recensione r " +
                "JOIN utente u1 ON r.recensore_id = u1.id " +
                "JOIN utente u2 ON r.recensito_id = u2.id " +
                "WHERE u2.email = ?";

        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql)) {

            query.setString(1, emailRecensito);
            try (ResultSet rs = query.executeQuery()) {
                while (rs.next()) {
                    Recensione r = new Recensione(
                            rs.getString("email_recensito"),
                            rs.getString("email_recensore"),
                            rs.getInt("voto")
                    );
                    r.setCommento(rs.getString("commento"));
                    r.setId(rs.getInt("id"));
                    recensioni.add(r);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return recensioni;
    }
}