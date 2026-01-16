package com.example.uninaswap.dao;

import java.sql.*;
import java.util.ArrayList;
import java.io.IOException;

import com.example.uninaswap.entity.Oggetto;
import com.example.uninaswap.entity.Utente;
import com.example.uninaswap.entity.Categoria;
import com.example.uninaswap.interfaces.GestoreOggettoDAO;

public class OggettoDAO implements GestoreOggettoDAO {

    private final OggettoCategoriaDAO oggettoCategoriaDAO = new OggettoCategoriaDAO();
    private final ImmagineDAO immagineDAO = new ImmagineDAO();

    // =================================================================================
    // LOGICA DI SALVATAGGIO (TRANSAZIONALE)
    // =================================================================================

    @Override
    public boolean salvaOggetto(Oggetto oggetto, Utente utente) {
        String sqlOggetto = "INSERT INTO OGGETTO (utente_id, nome, condizione, disponibilita) VALUES (?, ?, ?::condizione_oggetto, ?::disponibilita_oggetto)";
        Connection conn = null;

        try {
            conn = PostgreSQLConnection.getConnection();
            conn.setAutoCommit(false); // Inizio Transazione

            int idOggettoGenerato = -1;

            // 1. Inserimento dati base Oggetto
            try (PreparedStatement stmt = conn.prepareStatement(sqlOggetto, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, utente.getId());
                stmt.setString(2, oggetto.getNome());
                stmt.setString(3, toDbEnum(oggetto.getCondizione().name()));
                stmt.setString(4, toDbEnum(oggetto.getDisponibilita().name()));

                stmt.executeUpdate();
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        idOggettoGenerato = rs.getInt(1);
                        oggetto.setId(idOggettoGenerato);
                        System.out.println("DAO: Oggetto creato con ID: " + idOggettoGenerato);
                    }
                }
            }

            if (idOggettoGenerato == -1) throw new SQLException("Errore: ID oggetto non generato.");

            // 2. Gestione Fisica File e Database Immagini
            if (oggetto.getImmagini() != null && !oggetto.getImmagini().isEmpty()) {
                ArrayList<String> percorsiDefinitivi = new ArrayList<>();
                for (String pathTemporaneo : oggetto.getImmagini()) {
                    String pathRelativo = oggetto.copiaImmagineInLocale(pathTemporaneo);
                    if (pathRelativo != null) percorsiDefinitivi.add(pathRelativo);
                }

                oggetto.setImmagini(percorsiDefinitivi);
                if (!percorsiDefinitivi.isEmpty()) {
                    immagineDAO.inserisciImmaginiBatch(conn, idOggettoGenerato, percorsiDefinitivi);
                }
            }

            // 3. Associazione Categorie
            if (oggetto.getCategorie() != null && !oggetto.getCategorie().isEmpty()) {
                oggettoCategoriaDAO.associaCategorie(conn, idOggettoGenerato, oggetto.getCategorie());
            }

            conn.commit(); // Conferma Transazione
            System.out.println("DAO: Salvataggio completato con successo!");
            return true;

        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        } finally {
            chiudiConnessione(conn);
        }
    }

    @Override
    public boolean modificaOggetto(Oggetto oggetto) {
        String sqlUpdate = "UPDATE OGGETTO SET nome = ?, condizione = ?::condizione_oggetto, disponibilita = ?::disponibilita_oggetto WHERE id = ?";
        Connection conn = null;

        try {
            conn = PostgreSQLConnection.getConnection();
            conn.setAutoCommit(false);

            // A. Update dati base
            try (PreparedStatement stmt = conn.prepareStatement(sqlUpdate)) {
                stmt.setString(1, oggetto.getNome());
                stmt.setString(2, toDbEnum(oggetto.getCondizione().name()));
                stmt.setString(3, toDbEnum(oggetto.getDisponibilita().name()));
                stmt.setInt(4, oggetto.getId());
                stmt.executeUpdate();
            }

            // B. Update Categorie (Pulisci e reinserisci)
            oggettoCategoriaDAO.eliminaCategoriePerOggetto(conn, oggetto.getId());
            if (oggetto.getCategorie() != null && !oggetto.getCategorie().isEmpty()) {
                oggettoCategoriaDAO.associaCategorie(conn, oggetto.getId(), oggetto.getCategorie());
            }

            // C. Update Immagini
            immagineDAO.rimuoviImmaginiPerOggetto(conn, oggetto.getId());
            if (oggetto.getImmagini() != null && !oggetto.getImmagini().isEmpty()) {
                ArrayList<String> percorsiFinali = new ArrayList<>();
                for (String path : oggetto.getImmagini()) {
                    if (path != null && !path.startsWith("oggetti/")) {
                        String nuovoPath = oggetto.copiaImmagineInLocale(path);
                        if (nuovoPath != null) percorsiFinali.add(nuovoPath);
                    } else {
                        percorsiFinali.add(path);
                    }
                }
                immagineDAO.inserisciImmaginiBatch(conn, oggetto.getId(), percorsiFinali);
            }

            conn.commit();
            System.out.println("DAO: Oggetto aggiornato correttamente!");
            return true;
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        } finally {
            chiudiConnessione(conn);
        }
    }

    // =================================================================================
    // LOGICA DI RECUPERO DATI
    // =================================================================================

    public ArrayList<Oggetto> ottieniTuttiOggetti(int idUtente) {
        ArrayList<Oggetto> lista = new ArrayList<>();
        String sql = "SELECT * FROM OGGETTO WHERE utente_id = ? ORDER BY id DESC";
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUtente);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Oggetto obj = mapResultSetToOggetto(rs);
                    obj.setImmagini(immagineDAO.ottieniImmaginiStringhe(obj.getId()));
                    obj.setCategorie(oggettoCategoriaDAO.ottieniCategoriePerOggetto(obj.getId()));
                    lista.add(obj);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    @Override
    public boolean associaOgettiAdAnnuncio(ArrayList<Oggetto> oggetti, int idAnnuncio) {
        return false;
    }

    @Override
    public void associaListaOggetti(ArrayList<Oggetto> listaOggetti, int idAnnuncio) {

    }

    public ArrayList<Oggetto> ottieniTuttiOggettiDisponibili(int idUtente) {
        ArrayList<Oggetto> lista = new ArrayList<>();
        String sql = "SELECT * FROM OGGETTO WHERE utente_id = ? AND disponibilita = 'DISPONIBILE' ORDER BY id DESC";
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUtente);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Oggetto obj = mapResultSetToOggetto(rs);
                    obj.setImmagini(immagineDAO.ottieniImmaginiStringhe(obj.getId()));
                    obj.setCategorie(oggettoCategoriaDAO.ottieniCategoriePerOggetto(obj.getId()));
                    lista.add(obj);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public Oggetto ottieniOggetto(int idOggetto, Utente utente) {
        String sql = "SELECT * FROM OGGETTO WHERE id = ?";
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idOggetto);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Oggetto obj = mapResultSetToOggetto(rs);
                    obj.setProprietario(utente);
                    obj.setCategorie(oggettoCategoriaDAO.ottieniCategoriePerOggetto(idOggetto));
                    obj.setImmagini(immagineDAO.ottieniImmaginiStringhe(idOggetto));
                    return obj;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public boolean eliminaOggetto(int idOggetto) {
        String sql = "DELETE FROM OGGETTO WHERE id = ?";
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idOggetto);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean associaUtente(int idU, int idO) {
        return false;
    }

    @Override
    public boolean rimuoviDaUtente(int idU, int idO) {
        return false;
    }

    // =================================================================================
    // ASSOCIAZIONE ANNUNCI
    // =================================================================================

    public boolean associaAnnuncio(int idOggetto, int idAnnuncio) {
        String sql = "UPDATE OGGETTO SET annuncio_id = ? WHERE id = ?";
        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idAnnuncio);
            stmt.setInt(2, idOggetto);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean rimuoviDaAnnuncio(int idU, int idA) {
        return false;
    }

    // =================================================================================
    // HELPER PRIVATI
    // =================================================================================

    private Oggetto mapResultSetToOggetto(ResultSet rs) throws SQLException {
        Oggetto oggetto = new Oggetto();
        oggetto.setId(rs.getInt("id"));
        oggetto.setNome(rs.getString("nome"));

        String condStr = rs.getString("condizione");
        if(condStr != null) {
            oggetto.setCondizione(Oggetto.CONDIZIONE.valueOf(condStr.replace(" ", "_").toUpperCase()));
        }

        String dispStr = rs.getString("disponibilita");
        if(dispStr != null) {
            oggetto.setDisponibilita(Oggetto.DISPONIBILITA.valueOf(dispStr.replace(" ", "_").toUpperCase()));
        }
        return oggetto;
    }

    private String toDbEnum(String val) {
        return (val == null) ? null : val.replace("_", " ");
    }

    private void chiudiConnessione(Connection conn) {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}