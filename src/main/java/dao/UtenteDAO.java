package dao;

import java.sql.*;
import java.util.ArrayList;
import entity.*;
import interfaces.GestoreUtente;

public class UtenteDAO implements GestoreUtente {

    public boolean salvaUtente(Utente utente) {
        String sql = "INSERT INTO utente (username, password, matricola, email, pathImmagineProfilo) VALUES ( ?, ?, ?, ?, ?)";
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
        String sql = "UPDATE utente SET username = ?, password = ?, matricola = ?, email = ?, pathImmagineProfilo = ? WHERE id = ?";
        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql)) {
            query.setString(1, utente.getUsername());
            query.setString(2, utente.getPassword());
            query.setString(3, utente.getMatricola());
            query.setString(4, utente.getEmail());
            query.setString(5, utente.getPathImmagineProfilo());
            query.setInt(6, utente.getId());


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
             PreparedStatement query = connessione.prepareStatement(sql);) {
            query.setInt(1, id);
            try (ResultSet rs = query.executeQuery()) {
                if (rs.next()) {
                    utente = new Utente(rs.getString("username"), rs.getString("password"), rs.getString("matircola"), rs.getString("email"));
                    utente.setPathImmagineProfilo(rs.getString("pathImmagineProfilo"));
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
        String sql = "SELECT * FROM utente WHERE matricola = ?";
        try (Connection connessione = PostgreSQLConnection.getConnection();
             PreparedStatement query = connessione.prepareStatement(sql);) {
            query.setString(1, sql);
            int sceltaRicerca = 0;
            if (campoRicerca.contains("@") == true) {
                sceltaRicerca = 1;
            }
            switch (sceltaRicerca) {
                case 0: //ricerca tramite matricola
                    try (ResultSet rs = query.executeQuery()) {
                        if (rs.next()) {
                            utente = new Utente(rs.getString("username"), rs.getString("password"), campoRicerca, rs.getString("email"));
                            utente.setPathImmagineProfilo(rs.getString("pathImmagineProfilo"));
                            utente.setId(rs.getInt("id"));
                        }
                    }
                    break;
                case 1: //ricerca tramite mail
                    try (ResultSet rs = query.executeQuery()) {
                        if (rs.next()) {
                            utente = new Utente(rs.getString("username"), rs.getString("password"), rs.getString("matricola"), campoRicerca);
                            utente.setPathImmagineProfilo(rs.getString("pathImmagineProfilo"));
                            utente.setId(rs.getInt("id"));
                        }
                    }
                    break;
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
}
