package com.example.uninaswap.dao;

import java.sql.*;
import java.util.ArrayList;

import com.example.uninaswap.entity.Oggetto;
import com.example.uninaswap.entity.Utente;
import com.example.uninaswap.entity.Categoria;
import com.example.uninaswap.interfaces.GestoreOggettoDAO;

public class OggettoDAO implements GestoreOggettoDAO {

    // Istanze delle DAO ausiliarie
    private OggettoCategoriaDAO oggettoCategoriaDAO = new OggettoCategoriaDAO();
    // Assumo che ImmagineDAO esista e abbia il metodo inserisciImmaginiBatch
    private ImmagineDAO immagineDAO = new ImmagineDAO();

    /**
     * Salva Oggetto, Categorie e Immagini in un'unica Transazione.
     */
    public boolean salvaOggetto(Oggetto oggetto, Utente utente) {
        // Nota: ?::type è la sintassi di cast di PostgreSQL. Assicurati che i tipi esistano nel DB.
        String sqlOggetto = "INSERT INTO OGGETTO (utente_id, nome, condizione, disponibilita) VALUES (?, ?, ?::condizione_oggetto, ?::disponibilita_oggetto)";

        Connection conn = null;

        try {
            conn = PostgreSQLConnection.getConnection();
            conn.setAutoCommit(false); // --- INIZIO TRANSAZIONE ---

            // 1. Inserimento Oggetto e recupero ID
            int idOggettoGenerato = -1;
            try (PreparedStatement stmt = conn.prepareStatement(sqlOggetto, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, utente.getId());
                stmt.setString(2, oggetto.getNome());
                // Enum -> String per il DB
                stmt.setString(3, oggetto.getCondizione().name());
                stmt.setString(4, oggetto.getDisponibilita().name());

                int rows = stmt.executeUpdate();
                if (rows == 0) throw new SQLException("Inserimento oggetto fallito.");

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        idOggettoGenerato = generatedKeys.getInt(1);
                        oggetto.setId(idOggettoGenerato); // Aggiorniamo l'ID in memoria
                    } else {
                        throw new SQLException("Nessun ID generato per l'oggetto.");
                    }
                }
            }

            // 2. Inserimento Categorie (Tabella Ponte) usando la stessa connessione
            // FIX: Chiamata corretta al metodo che accetta (Connection, int, ArrayList)
            oggettoCategoriaDAO.associaCategorie(conn, idOggettoGenerato, oggetto.getCategorie());

            // 3. Inserimento Immagini usando la stessa connessione
            if (oggetto.getImmagini() != null && !oggetto.getImmagini().isEmpty()) {
                immagineDAO.inserisciImmaginiBatch(conn, idOggettoGenerato, oggetto.getImmagini());
            }

            conn.commit(); // --- CONFERMA TRANSAZIONE ---
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    System.err.println("Eseguo Rollback per errore: " + e.getMessage());
                    conn.rollback(); // --- ANNULLA TUTTO IN CASO DI ERRORE ---
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Recupera un oggetto completo (Dati Base + Categorie + Immagini).
     */
    public Oggetto ottieniOggetto(int idOggetto, Utente utente) {
        String sql = "SELECT * FROM OGGETTO WHERE id = ?";
        Oggetto oggetto = null;

        try (Connection conn = PostgreSQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idOggetto);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    oggetto = new Oggetto();
                    oggetto.setId(rs.getInt("id"));
                    oggetto.setNome(rs.getString("nome"));

                    // Enum Conversion (String DB -> Enum Java)
                    String condStr = rs.getString("condizione");
                    if(condStr != null) oggetto.setCondizione(Oggetto.CONDIZIONE.valueOf(condStr));

                    String dispStr = rs.getString("disponibilita");
                    if(dispStr != null) oggetto.setDisponibilita(Oggetto.DISPONIBILITA.valueOf(dispStr));

                    oggetto.setProprietario(utente);

                    // 1. Recupero Categorie (tramite DAO ponte)
                    ArrayList<Categoria> categorie = oggettoCategoriaDAO.ottieniCategoriePerOggetto(idOggetto);
                    oggetto.setCategorie(categorie);

                    // 2. Recupero Immagini (tramite DAO Immagine)
                    ArrayList<String> immagini = immagineDAO.ottieniImmaginiStringhe(idOggetto);
                    oggetto.setImmagini(immagini);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return oggetto;
    }

    // Metodi da implementare se servono
    public boolean modificaOggetto(Oggetto oggetto) { return true; }
    public boolean eliminaOggetto(int idOggetto) { return true; }
    public boolean associaUtente(int idUtente, int idOggetto) { return true; }
    public boolean rimuoviDaUtente(int idUtente, int idOggetto) { return true; }
    public boolean associaAnnuncio(int idUtente, int idAnnuncio) { return true; }
    public boolean rimuoviDaAnnuncio(int idUtente, int idAannuncio) { return true; }
    public ArrayList<Oggetto> ottieniTuttiOggetti() { return null; }
}