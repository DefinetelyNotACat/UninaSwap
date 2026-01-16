package com.example.uninaswap.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.example.uninaswap.entity.Recensione;
import com.example.uninaswap.interfaces.GestoreRecensioneDAO;

public class RecensioneDAO implements GestoreRecensioneDAO {

    // Query base che unisce le tabelle per recuperare le email partendo dagli ID
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

    /**
     * Recupera tutte le recensioni che un utente ha RICEVUTO.
     * USA LOWER e TRIM per ignorare spazi e maiuscole/minuscole.
     */
    public ArrayList<Recensione> ottieniRecensioniPerUtente(String emailRecensito) {
        ArrayList<Recensione> recensioni = new ArrayList<>();
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

    /**
     * Cerca una recensione specifica fatta da un utente ad un altro.
     * ESSENZIALE per far funzionare l'UPDATE invece della INSERT.
     */
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
        // Mappa le email agli ID corretti ignorando spazi e casing
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
            System.err.println("ERRORE IN SALVA_RECENSIONE: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean ModificaRecensione(Recensione recensione) {
        // L'ID deve essere quello trovato con OttieniRecensioneTraUtenti
        String sql = "UPDATE recensione SET voto = ?, commento = ? WHERE id = ?";
        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql)) {

            query.setInt(1, recensione.getVoto());
            query.setString(2, recensione.getCommento());
            query.setInt(3, recensione.getId());

            int righeModificate = query.executeUpdate();
            System.out.println("DEBUG DAO: Eseguito UPDATE su ID " + recensione.getId() + ". Esito: " + (righeModificate > 0));
            return righeModificate > 0;
        } catch (Exception e) {
            System.err.println("ERRORE IN MODIFICA_RECENSIONE: " + e.getMessage());
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

    /**
     * Mappa i dati del database all'oggetto Recensione popolando l'ID per future modifiche.
     */
    private Recensione mapResultSetToRecensione(ResultSet rs) throws SQLException {
        Recensione r = new Recensione(
                rs.getString("email_recensito"),
                rs.getString("email_recensore"),
                rs.getInt("voto")
        );
        r.setCommento(rs.getString("commento"));
        r.setId(rs.getInt("id")); // Fondamentale: senza questo l'UPDATE non sa dove colpire
        return r;
    }
}