package com.example.uninaswap.dao;

import java.sql.*;
import java.util.ArrayList;

import com.example.uninaswap.entity.Utente;
import com.example.uninaswap.interfaces.GestoreUtenteDAO;

public class UtenteDAO implements GestoreUtenteDAO {

    public boolean salvaUtente(Utente utente) {
        String sql = "INSERT INTO utente (username, password, matricola, email, immagine_profilo) VALUES ( ?, ?, ?, ?, ?)";
        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql)) {
            query.setString(1, utente.getUsername());
            query.setString(2, utente.getPassword());
            query.setString(3, utente.getMatricola());
            query.setString(4, utente.getEmail());
            query.setString(5, utente.getPathImmagineProfilo());

            int numModifiche = query.executeUpdate();
            return numModifiche > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean modificaUtente(Utente utente) {
        String sql = "UPDATE utente SET username = ?, password = ?, matricola = ?, email = ?, immagine_profilo = ? WHERE email = ?";
        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql)) {
            query.setString(1, utente.getUsername());
            query.setString(2, utente.getPassword());
            query.setString(3, utente.getMatricola());
            query.setString(4, utente.getEmail());
            query.setString(5, utente.getPathImmagineProfilo());
            query.setString(6, utente.getEmail());
            int numModifiche = query.executeUpdate();
            return numModifiche > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean eliminaUtente(int id) {
        String sql = "DELETE FROM utente WHERE id = ?";
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

    public Utente ottieniUtente(int id) {
        Utente utente = null;
        String sql = "SELECT * FROM utente WHERE id = ?";
        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql)) {

            query.setInt(1, id);
            try (ResultSet rs = query.executeQuery()) {
                if (rs.next()) {
                    utente = new Utente(
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("matricola"),
                            rs.getString("email")
                    );
                    utente.setPathImmagineProfilo(rs.getString("immagine_profilo"));
                    utente.setId(id);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return utente;
    }
    public Utente ottieniUtente(String campoRicerca) {
        Utente utente = null;
        String sql;

        // 1. Decidiamo la query in base al contenuto della stringa
        if (campoRicerca.contains("@")) {
            sql = "SELECT * FROM utente WHERE email = ?";
        } else {
            sql = "SELECT * FROM utente WHERE matricola = ?";
        }

        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql)) {

            query.setString(1, campoRicerca);

            try (ResultSet rs = query.executeQuery()) {
                if (rs.next()) {
                    utente = new Utente(
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("matricola"),
                            rs.getString("email")
                    );

                    // 3. Corretto il nome colonna del DB: "immagine_profilo" invece di "pathImmagineProfilo"
                    utente.setPathImmagineProfilo(rs.getString("immagine_profilo"));
                    utente.setId(rs.getInt("id"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return utente;
    }
    public ArrayList<Utente> ottieniTuttiUtenti() {
        ArrayList<Utente> tuttiUtenti = new ArrayList<>();
        String sql = "SELECT * FROM utente";
        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql);
             ResultSet rs = query.executeQuery()) {
            while (rs.next()) {
                Utente utente = new Utente(rs.getString("username"), rs.getString("password"), rs.getString("matricola"), rs.getString("email"));
                utente.setPathImmagineProfilo(rs.getString("pathImmagineProfilo"));
                utente.setId(rs.getInt("id"));
                tuttiUtenti.add(utente);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tuttiUtenti;
    }
    // AGGIUNGI QUESTO METODO IN: com.example.uninaswap.dao.UtenteDAO

    public boolean verificaEsistenzaAltroUtente(String username, String emailDaEscludere, String matricola) {
        // Seleziona se esiste qualcuno con questo username O matricola, MA che non abbia la mia email
        String sql = "SELECT COUNT(*) FROM utente WHERE (username = ? OR matricola = ?) AND email <> ?";

        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql)) {

            query.setString(1, username);
            query.setString(2, matricola);
            query.setString(3, emailDaEscludere);

            try (ResultSet rs = query.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean verificaEsistenzaUtenteRegistrazione(String username, String email, String matricola) {
        // Logica per REGISTRAZIONE:
        // Controlla se ESISTE GIÀ una riga che ha:
        // Quello Username OPPURE Quella Matricola OPPURE Quella Email
        String sql = "SELECT COUNT(*) FROM utente WHERE username = ? OR email = ? OR matricola = ?";

        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql)) {

            query.setString(1, username);
            query.setString(2, email);
            query.setString(3, matricola);

            try (ResultSet rs = query.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

}
