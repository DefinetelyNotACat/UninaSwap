package com.example.uninaswap.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.example.uninaswap.entity.Recensione;
import com.example.uninaswap.interfaces.GestoreRecensioneDAO;

public class RecensioneDAO implements GestoreRecensioneDAO {

    // Query base con JOIN per trasformare gli ID in email
    private static final String SELECT_BASE =
            "SELECT r.*, u1.email AS email_recensore, u2.email AS email_recensito " +
                    "FROM recensione r " +
                    "JOIN utente u1 ON r.recensore_id = u1.id " +
                    "JOIN utente u2 ON r.recensito_id = u2.id";

    @Override
    public Recensione OttieniRecensione(int id) {
        Recensione recensione = null;
        String sql = SELECT_BASE + " WHERE r.id = ?";
        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql)) {

            query.setInt(1, id);
            try (ResultSet rs = query.executeQuery()) {
                if (rs.next()) {
                    recensione = mapResultSetToRecensione(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return recensione;
    }

    @Override
    public List<Recensione> OttieniTutteRecensione() {
        ArrayList<Recensione> tutteRecensione = new ArrayList<>();
        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(SELECT_BASE);
             ResultSet rs = query.executeQuery()) {

            while (rs.next()) {
                tutteRecensione.add(mapResultSetToRecensione(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tutteRecensione;
    }

    public ArrayList<Recensione> ottieniRecensioniPerUtente(String emailRecensito) {
        ArrayList<Recensione> recensioni = new ArrayList<>();
        // u2 è l'utente RECENSITO nella SELECT_BASE
        String sql = SELECT_BASE + " WHERE LOWER(TRIM(u2.email)) = LOWER(TRIM(?))";

        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql)) {

            query.setString(1, emailRecensito);
            try (ResultSet rs = query.executeQuery()) {
                while (rs.next()) {
                    recensioni.add(mapResultSetToRecensione(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return recensioni;
    }

    public ArrayList<Recensione> ottieniRecensioniFatteDaUtente(String emailRecensore) {
        ArrayList<Recensione> lista = new ArrayList<>();
        // u1 è l'utente RECENSORE nella SELECT_BASE
        String sql = SELECT_BASE + " WHERE LOWER(TRIM(u1.email)) = LOWER(TRIM(?))";

        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, emailRecensore);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapResultSetToRecensione(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("ERRORE IN ottieniRecensioniFatteDaUtente: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    public Recensione OttieniRecensioneTraUtenti(String emailRecensore, String emailRecensito) {
        Recensione recensione = null;
        String sql = SELECT_BASE + " WHERE LOWER(TRIM(u1.email)) = LOWER(TRIM(?)) " +
                "AND LOWER(TRIM(u2.email)) = LOWER(TRIM(?))";

        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql)) {

            query.setString(1, emailRecensore);
            query.setString(2, emailRecensito);

            try (ResultSet rs = query.executeQuery()) {
                if (rs.next()) {
                    recensione = mapResultSetToRecensione(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return recensione;
    }

    @Override
    public boolean SalvaRecensione(Recensione recensione) {
        String sql = "INSERT INTO recensione (voto, commento, recensore_id, recensito_id) " +
                "VALUES (?, ?, (SELECT id FROM utente WHERE LOWER(TRIM(email)) = LOWER(TRIM(?))), " +
                "(SELECT id FROM utente WHERE LOWER(TRIM(email)) = LOWER(TRIM(?))))";

        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql)) {

            query.setInt(1, recensione.getVoto());
            query.setString(2, recensione.getCommento());
            query.setString(3, recensione.getEmailRecensore());
            query.setString(4, recensione.getEmailRecensito());

            return query.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean ModificaRecensione(Recensione recensione) {
        String sql = "UPDATE recensione SET voto = ?, commento = ? WHERE id = ?";
        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql)) {

            query.setInt(1, recensione.getVoto());
            query.setString(2, recensione.getCommento());
            query.setInt(3, recensione.getId());

            return query.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean EliminaRecensione(int id) {
        String sql = "DELETE FROM recensione WHERE id = ?";
        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql)) {

            query.setInt(1, id);
            return query.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private Recensione mapResultSetToRecensione(ResultSet rs) throws SQLException {
        Recensione r = new Recensione(
                rs.getString("email_recensito"),
                rs.getString("email_recensore"),
                rs.getInt("voto")
        );
        r.setCommento(rs.getString("commento"));
        r.setId(rs.getInt("id"));
        return r;
    }

}