package com.example.uninaswap.dao;

import java.sql.*;
import java.util.ArrayList;
import com.example.uninaswap.entity.Utente;
import com.example.uninaswap.entity.Annuncio;

import com.example.uninaswap.interfaces.GestoreUtenteDAO;
import org.jetbrains.annotations.NotNull;

public class UtenteDAO implements GestoreUtenteDAO {

    /**
     * Salva un nuovo utente nel database.
     */
    public boolean salvaUtente(Utente utente) {
        String sql = "INSERT INTO utente (username, password, matricola, email, immagine_profilo) VALUES (?, ?, ?, ?, ?)";
        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql)) {

            query.setString(1, utente.getUsername());
            query.setString(2, utente.getPassword());
            query.setString(3, utente.getMatricola());
            query.setString(4, utente.getEmail());
            query.setString(5, utente.getPathImmagineProfilo());

            return query.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Modifica i dati di un utente esistente basandosi sulla email.
     */
    public boolean modificaUtente(Utente utente) {
        String sql = "UPDATE utente SET username = ?, password = ?, matricola = ?, immagine_profilo = ? WHERE email = ?";
        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql)) {

            query.setString(1, utente.getUsername());
            query.setString(2, utente.getPassword());
            query.setString(3, utente.getMatricola());
            query.setString(4, utente.getPathImmagineProfilo());
            query.setString(5, utente.getEmail());

            return query.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Elimina un utente dal database tramite ID.
     */
    public boolean eliminaUtente(int id) {
        String sql = "DELETE FROM utente WHERE id = ?";
        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql)) {

            query.setInt(1, id);
            return query.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Recupera un utente tramite il suo ID numerico.
     */
    @Override
    public Utente ottieniUtente(int id) {
        String sql = "SELECT * FROM utente WHERE id = ?";
        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql)) {

            query.setInt(1, id);
            try (ResultSet rs = query.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUtente(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Ricerca ESATTA per username (Case-Sensitive).
     */
    public Utente trovaUtenteUsername(String username) {
        // MODIFICA QUI: Usiamo LOWER() su entrambi i lati del confronto
        String sql = "SELECT * FROM utente WHERE LOWER(username) = LOWER(?)";

        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql)) {

            // Passiamo lo username così com'è, SQL si occuperà di renderlo minuscolo per il confronto
            query.setString(1, username.trim());

            try (ResultSet rs = query.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUtente(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Utente> cercaUtentiByUsername(String parteUsername, int idDaEscludere) {
        ArrayList<Utente> risultati = new ArrayList<>();

        // Aggiungiamo "AND id != ?" alla query
        String sql = "SELECT * FROM utente WHERE LOWER(username) LIKE LOWER(?) AND id != ?";

        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql)) {

            // Parametro 1: Il testo da cercare con i caratteri jolly
            query.setString(1, "%" + parteUsername.trim() + "%");

            // Parametro 2: Il tuo ID, per escluderti dai risultati
            query.setInt(2, idDaEscludere);

            try (ResultSet rs = query.executeQuery()) {
                while (rs.next()) {
                    risultati.add(mapResultSetToUtente(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return risultati;
    }

    /**
     * Ricerca tramite email o matricola.
     */
    public Utente ottieniUtente(String campoRicerca) {
        String sql = campoRicerca.contains("@") ?
                "SELECT * FROM utente WHERE email = ?" :
                "SELECT * FROM utente WHERE matricola = ?";

        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql)) {

            query.setString(1, campoRicerca.contains("@") ? campoRicerca.toLowerCase().trim() : campoRicerca);

            try (ResultSet rs = query.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUtente(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Recupera la lista di tutti gli utenti registrati.
     */
    public ArrayList<Utente> ottieniTuttiUtenti() {
        ArrayList<Utente> tuttiUtenti = new ArrayList<>();
        String sql = "SELECT * FROM utente";
        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql);
             ResultSet rs = query.executeQuery()) {

            while (rs.next()) {
                tuttiUtenti.add(mapResultSetToUtente(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tuttiUtenti;
    }

    /**
     * Verifica se esistono duplicati durante la modifica del profilo.
     */
    public boolean verificaEsistenzaAltroUtente(String username, String matricola, String emailDaEscludere) {
        String sql = "SELECT COUNT(*) FROM utente WHERE (username = ? OR matricola = ?) AND email <> ?";
        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql)) {

            query.setString(1, username);
            query.setString(2, matricola);
            query.setString(3, emailDaEscludere.toLowerCase().trim());

            try (ResultSet rs = query.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Verifica conflitti durante la registrazione.
     */
    public void verificaEsistenzaUtenteRegistrazione(String username, String email, String matricola) throws Exception {
        String sql = "SELECT username, email, matricola FROM utente WHERE username = ? OR email = ? OR matricola = ?";
        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql)) {

            query.setString(1, username);
            query.setString(2, email.toLowerCase().trim());
            query.setString(3, matricola);

            try (ResultSet rs = query.executeQuery()) {
                if (rs.next()) {
                    if (matricola.equals(rs.getString("matricola"))) throw new Exception("Matricola già presente");
                    if (username.equals(rs.getString("username"))) throw new Exception("Username già preso");
                    if (email.equalsIgnoreCase(rs.getString("email"))) throw new Exception("Email già registrata");
                }
            }
        }
    }

    /**
     * Metodo Helper per mappare il ResultSet in un oggetto Utente ed evitare duplicazione di codice.
     */
    private Utente mapResultSetToUtente(ResultSet rs) throws SQLException {
        Utente u = new Utente(
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("matricola"),
                rs.getString("email")
        );
        u.setPathImmagineProfilo(rs.getString("immagine_profilo"));
        u.setId(rs.getInt("id"));
        return u;
    }

}